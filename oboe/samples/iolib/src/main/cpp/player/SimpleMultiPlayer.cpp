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
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>
#include <cmath>
#include <utility>
//#include <chrono>

// local includes
#include "OneShotSampleSource.h"
#include "SimpleMultiPlayer.h"

static const char *TAG = "SimpleMultiPlayer";

using namespace oboe;
using namespace parselib;

namespace iolib {

    constexpr int32_t kBufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)

    SimpleMultiPlayer::SimpleMultiPlayer() : mMixBuffer(nullptr), mChannelCount(0), mSampleRate(0),
                                             mOutputReset(false), mDecayFactor(1.0f) {}

    DataCallbackResult SimpleMultiPlayer::onAudioReady(AudioStream *oboeStream, void *audioData,
                                                       int32_t numFrames) {

        StreamState streamState = oboeStream->getState();
        if (streamState != StreamState::Open && streamState != StreamState::Started) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState:%d", streamState);
        }
        if (streamState == StreamState::Disconnected) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "  streamState::Disconnected");
        }
        memset(audioData, 0, numFrames * mChannelCount * sizeof(float));
        mixAudioToBuffer((float *) audioData, numFrames);

        if (pSynth != nullptr && mChannelCount == 2) {
            fluid_synth_write_float(pSynth, numFrames, audioData, 0, 2, audioData, 1, 2);
        }

        if (record) {
            mRecordingIO->write_buffer(mMixBuffer, numFrames);
        }
        return DataCallbackResult::Continue;
    }

    void SimpleMultiPlayer::mixAudioToBuffer(float *audioData, int32_t numFrames) {
//        auto mid1 = std::chrono::high_resolution_clock::now();
        if (mMixBuffer == nullptr) {
            mMixBuffer = new float[mAudioStream->getBufferSizeInFrames()];
        }
        memset(mMixBuffer, 0, numFrames * mChannelCount * sizeof(float));
        float sampleCount = 0;
        for (int32_t index = 0; index < mNumSampleBuffers; index++) {
            SampleSource *sampleSource = mSampleSources[index];
            int32_t queueSize = sampleSource->getCurFrameIndexQueueSize();
            int32_t numSampleFrames = mSampleBuffers[index]->getNumSampleFrames();
            for (int32_t i = 0; i < queueSize; i++) {
                std::__ndk1::pair<int32_t, int32_t> *curFrameIndex = sampleSource->frontCurFrameIndexQueue();
                sampleSource->mixAudio(mMixBuffer, mChannelCount, numFrames, curFrameIndex);
                memcpy(audioData, mMixBuffer, numFrames * mChannelCount * sizeof(float));
                if (curFrameIndex != nullptr && (*curFrameIndex).first >= numSampleFrames) {
                    // this sample is finished
                    sampleSource->popCurFrameIndexQueue();
                } else {
                    sampleCount += 1;
                    // the size of queue equals one, can avoid moving queue
                    if (curFrameIndex != nullptr && queueSize > 1) {
                        sampleSource->popCurFrameIndexQueue();
                        sampleSource->pushCurFrameIndexQueue(*curFrameIndex);
                    }
                }
            }
        }
//        auto mid2 = std::chrono::high_resolution_clock::now();
//        __android_log_print(ANDROID_LOG_WARN, TAG, "mixAudioToBuffer oboeDuration %lld",
//                            std::chrono::duration_cast<std::chrono::microseconds>(mid2 - mid1).count());

        // Divide value by the logarithm of the "total number of samples"
        // ensure that the volume is not too high when too many samples
        float logSampleCount = log(sampleCount + (float) exp(2)) - 1;
        for (int32_t i = 0; i < numFrames * mChannelCount; i++) {
            mMixBuffer[i] /= mDecayFactor;
            mDecayFactor += (logSampleCount - mDecayFactor) / 256;
        }
        memcpy(audioData, mMixBuffer, numFrames * mChannelCount * sizeof(float));
    }

    void SimpleMultiPlayer::onErrorAfterClose(AudioStream *oboeStream, Result error) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "==== onErrorAfterClose() error:%d", error);

        resetAll();
        openStream();
        mOutputReset = true;
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
        builder.setPerformanceMode(PerformanceMode::LowLatency);
        builder.setSharingMode(SharingMode::Exclusive);
        builder.setSampleRateConversionQuality(SampleRateConversionQuality::Medium);

        Result result = builder.openManagedStream(mAudioStream);
        if (result != Result::OK) {
            __android_log_print(
                    ANDROID_LOG_ERROR,
                    TAG,
                    "openStream failed. Error: %s", convertToText(result));
            return false;
        }

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
        if (index < mNumSampleBuffers) {
            mSampleSources[index]->setPlayMode(volume);
        }
    }

    void SimpleMultiPlayer::triggerUp(int32_t index) {
        if (index < mNumSampleBuffers) {
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
        record = r;
    }

    void SimpleMultiPlayer::setRecordFilePath(char *s) {
        mRecordingIO->setRecordingFilePath(s);
    }

    void SimpleMultiPlayer::setSf2SynthPtr(_fluid_synth_t *synth) {
        this->pSynth = synth;
    }
}
