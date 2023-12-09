/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <android/log.h>

// parselib includes
#include <wav/WavStreamReader.h>
#include <cmath>
#include <utility>

// local includes
#include "OneShotSampleSource.h"
#include "SimpleMultiPlayer.h"

static const char *TAG = "SimpleMultiPlayer";

using namespace std;
using namespace oboe;
using namespace parselib;

namespace iolib {

    constexpr int32_t kBufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)

    SimpleMultiPlayer::SimpleMultiPlayer() : mMixBuffer(nullptr), mChannelCount(0), mSampleRate(0),
                                             mDecayFactor(1.0f) {}

    DataCallbackResult SimpleMultiPlayer::onAudioReady(AudioStream *oboeStream, void *audioData,
                                                       int32_t numFrames) {
        StreamState streamState = oboeStream->getState();
        if (streamState != StreamState::Open && streamState != StreamState::Started) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState:%d", streamState);
        }
        if (streamState == StreamState::Disconnected) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState::Disconnected");
        }
        if (mMixBuffer == nullptr) {
            mMixBuffer = new float[mAudioStream->getBufferSizeInFrames()];
        }
        memset(audioData, 0, numFrames * mChannelCount * sizeof(float));
        if (mFluidHandle != nullptr && mFluidHandle->synth != nullptr && !mFluidHandle->loading) {
            if (mFluidHandle->soundfont_id > 0) {
                fluid_synth_write_float(mFluidHandle->synth, numFrames, (float *) audioData,
                                        0, 2, (float *) audioData, 1, 2);
                memcpy(mMixBuffer, ((float *) audioData),
                       numFrames * mChannelCount * sizeof(float));
                handleSf2DelayNoteOff(numFrames);
            } else {
                mixAudioToBuffer((float *) audioData, numFrames);
            }
            if (mRecord) {
                mRecordingIO->write_buffer(mMixBuffer, numFrames);
            }
        }
        return DataCallbackResult::Continue;
    }

    void SimpleMultiPlayer::handleSf2DelayNoteOff(int32_t numFrames) {
        for (int32_t index = 0; index < mNumSampleBuffers; index++) {
            shared_ptr<vector<tuple<int32_t, float, bool>>> noteVector = mSampleSources[index]->getCurFrameIndexVector();
            if (!noteVector->empty()) {
                tuple<int32_t, float, bool> &tuple = noteVector->front();
                if (get<2>(tuple)) {
                    if (get<0>(tuple) <= mDelayValue * 1000) {
                        get<0>(tuple) += numFrames;
                    } else {
                        fluid_synth_noteoff(mFluidHandle->synth, 0, 108 - index);
                        noteVector->clear();
                    }
                }
            }
        }
    }

    void SimpleMultiPlayer::mixAudioToBuffer(float *audioData, int32_t numFrames) {
        memset(mMixBuffer, 0, numFrames * mChannelCount * sizeof(float));
        float sampleCount = 0;
        for (int32_t index = 0; index < mNumSampleBuffers; index++) {
            SampleSource *sampleSource = mSampleSources[index];
            int32_t numSampleFrames = mSampleBuffers[index]->getNumSampleFrames();
            shared_ptr<vector<tuple<int32_t, float, bool>>> noteVector = sampleSource->getCurFrameIndexVector();
            auto lastIndex = static_cast<int32_t>(noteVector->size() - 1);
            int32_t handledIndex = max(0, lastIndex - 16);
            for (auto i = lastIndex; i >= handledIndex; i--) {
                tuple<int32_t, float, bool> *noteInfoTuple = &(*noteVector)[i];
                sampleSource->mixAudio(mMixBuffer, mChannelCount, mDelayVolumeFactor,
                                       numFrames, noteInfoTuple);
                memcpy(audioData, mMixBuffer, numFrames * mChannelCount * sizeof(float));
                if (get<0>(*noteInfoTuple) >= numSampleFrames || get<1>(*noteInfoTuple) <= 0) {
                    noteVector->erase(noteVector->begin() + i);
                } else {
                    sampleCount++;
                }
            }
            if (handledIndex > 0) {
                noteVector->erase(noteVector->begin(), noteVector->begin() + handledIndex);
            }
        }
        // Divide value by the logarithm of the "total number of samples"
        // ensure that the volume is not too high when too many samples
        float logSampleCount = log(sampleCount + (float) exp(2)) - 1;
        mDecayFactor += (logSampleCount - mDecayFactor) / 64;
        int32_t frameSampleCount = numFrames * mChannelCount;
        for (int32_t i = 0; i < frameSampleCount; i += mChannelCount) {
            mMixBuffer[i] /= mDecayFactor;
            mMixBuffer[i + 1] = mMixBuffer[i];
            // reverb compute algorithm
            if (mReverbValue != 0) {
                mMixBuffer[i] = mCombFilter->process(mMixBuffer[i])
                                + mAllPassFilter->process(mMixBuffer[i]);
                mMixBuffer[i + 1] = mMixBuffer[i];
            }
        }
        memcpy(audioData, mMixBuffer, numFrames * mChannelCount * sizeof(float));
    }

    void SimpleMultiPlayer::onErrorAfterClose(AudioStream *oboeStream, Result error) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "==== onErrorAfterClose() error:%d", error);

        resetAll();
        openStream();
    }

    void SimpleMultiPlayer::onErrorBeforeClose(AudioStream *, Result error) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "==== onErrorBeforeClose() error:%d", error);
    }

    bool SimpleMultiPlayer::openStream() {
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream()");

        // Create an audio stream
        AudioStreamBuilder builder;
        builder.setChannelCount(mChannelCount);
        builder.setSampleRate(mSampleRate);
        builder.setCallback(this);
        builder.setFormat(oboe::AudioFormat::Float);
        builder.setPerformanceMode(PerformanceMode::LowLatency);
        builder.setSharingMode(SharingMode::Exclusive);
        builder.setSampleRateConversionQuality(SampleRateConversionQuality::Medium);

        Result result = builder.openStream(mAudioStream);
        if (result != Result::OK) {
            __android_log_print(
                    ANDROID_LOG_ERROR,
                    TAG,
                    "openStream failed. Error: %s", convertToText(result));
            return false;
        }

        // Enable a device specific CPU performance hint.
        mAudioStream->setPerformanceHintEnabled(true);

        // Reduce stream latency by setting the buffer size to a multiple of the burst size
        // Note: this will fail with ErrorUnimplemented if we are using a callback with OpenSL ES
        // See oboe::AudioStreamBuffered::setBufferSizeInFrames
        result = mAudioStream->setBufferSizeInFrames(
                mAudioStream->getFramesPerBurst() * kBufferSizeInBursts);
        if (result != Result::OK) {
            __android_log_print(
                    ANDROID_LOG_WARN,
                    TAG,
                    "setBufferSizeInFrames failed. Error: %s", convertToText(result));
        }

        result = mAudioStream->requestStart();
        if (result != Result::OK) {
            __android_log_print(
                    ANDROID_LOG_ERROR,
                    TAG,
                    "requestStart failed. Error: %s", convertToText(result));
            return false;
        }

        return true;
    }

    void SimpleMultiPlayer::setupAudioStream(int32_t channelCount, int32_t sampleRate) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "setupAudioStream()");
        mChannelCount = channelCount;
        mSampleRate = sampleRate;
        mRecordingIO->init(channelCount, sampleRate);
        openStream();
    }

    void SimpleMultiPlayer::teardownAudioStream() {
        __android_log_print(ANDROID_LOG_INFO, TAG, "teardownAudioStream()");
        // tear down the player
        if (mAudioStream != nullptr) {
            mAudioStream->stop();
        }
    }

    void SimpleMultiPlayer::addSampleSource(SampleSource *source, SampleBuffer *buffer) {
        mSampleBuffers.push_back(buffer);
        mSampleSources.push_back(source);
        mNumSampleBuffers++;
    }

    void SimpleMultiPlayer::unloadSampleData() {
        __android_log_print(ANDROID_LOG_INFO, TAG, "unloadSampleData()");
        resetAll();

        for (int32_t bufferIndex = 0; bufferIndex < mNumSampleBuffers; bufferIndex++) {
            delete mSampleBuffers[bufferIndex];
            delete mSampleSources[bufferIndex];
        }

        mSampleBuffers.clear();
        mSampleSources.clear();

        mNumSampleBuffers = 0;
    }

    void SimpleMultiPlayer::triggerDown(int32_t index, int32_t volume) {
        if (index >= 0 && index < mNumSampleBuffers && volume > 0) {
            mSampleSources[index]->setPlayMode(volume);
        }
    }

    void SimpleMultiPlayer::triggerDownSingle(int32_t index, int32_t volume) {
        if (index >= 0 && index < mNumSampleBuffers && volume > 0) {
            mSampleSources[index]->setPlaySingleMode(volume);
        }
    }

    void SimpleMultiPlayer::triggerUp(int32_t index) {
        if (index >= 0 && index < mNumSampleBuffers) {
            mSampleSources[index]->setStopMode();
        }
    }

    void SimpleMultiPlayer::resetAll() {
        for (int32_t bufferIndex = 0; bufferIndex < mNumSampleBuffers; bufferIndex++) {
            mSampleSources[bufferIndex]->setStopMode();
        }
    }

    void SimpleMultiPlayer::setRecord(bool r) {
        if (r) {
            mRecordingIO->reserveRecordingBuffer(mSampleRate);
        } else {
            mRecordingIO->clearRecordingBuffer();
        }
        mRecord = r;
    }

    void SimpleMultiPlayer::setRecordFilePath(const char *s) {
        mRecordingIO->setRecordingFilePath(s);
    }

    void SimpleMultiPlayer::setSf2Synth(fluid_handle_t *fluidHandle) {
        this->mFluidHandle = fluidHandle;
    }

    void SimpleMultiPlayer::setReverbValue(int32_t reverb) {
        this->mReverbValue = reverb;
        this->mCombFilter->setGain((float) reverb / 100);
        this->mCombFilter->setDelay(reverb * 10);
        this->mAllPassFilter->setGain((float) reverb / 100);
        this->mAllPassFilter->setDelay(reverb * 10);
    }

    int32_t SimpleMultiPlayer::getReverbValue() const {
        return this->mReverbValue;
    }

    void SimpleMultiPlayer::setDelayValue(int32_t delay) {
        this->mDelayValue = delay;
        this->mDelayVolumeFactor = 2e-3f / ((float) delay * 3 + 50);
    }
}
