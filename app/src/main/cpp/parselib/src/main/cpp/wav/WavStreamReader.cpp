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
#include <algorithm>
#include <cstring>
#include <android/log.h>

#include "stream/InputStream.h"

#include "AudioEncoding.h"
#include "WavRIFFChunkHeader.h"
#include "WavFmtChunkHeader.h"
#include "WavChunkHeader.h"
#include "WavStreamReader.h"

static const char *TAG = "WavStreamReader";

namespace parselib {

    WavStreamReader::WavStreamReader(InputStream *stream) {
        mStream = stream;

        mWavChunk = nullptr;
        mFmtChunk = nullptr;
        mDataChunk = nullptr;

        mAudioDataStartPos = -1;

        mChunkMap = new std::map<RiffID, WavChunkHeader *>();
    }

    int WavStreamReader::getSampleEncoding() {
        if (mFmtChunk->mEncodingId == WavFmtChunkHeader::ENCODING_PCM) {
            switch (mFmtChunk->mSampleSize) {
                case 8:
                    return AudioEncoding::PCM_8;

                case 16:
                    return AudioEncoding::PCM_16;

                case 24:
                    // TODO - Support 24-bit WAV data
                    return AudioEncoding::INVALID; // for now

                default:
                    return AudioEncoding::INVALID;
            }
        } else if (mFmtChunk->mEncodingId == WavFmtChunkHeader::ENCODING_IEEE_FLOAT) {
            return AudioEncoding::PCM_IEEEFLOAT;
        }

        return AudioEncoding::INVALID;
    }

    void WavStreamReader::parse() {
        RiffID tag;

        while (true) {
            int numRead = mStream->peek(&tag, sizeof(tag));
            if (numRead <= 0) {
                break; // done
            }

            WavChunkHeader *chunk;
            if (tag == WavRIFFChunkHeader::RIFFID_RIFF) {
                chunk = mWavChunk = new WavRIFFChunkHeader(tag);
                mWavChunk->read(mStream);
            } else if (tag == WavFmtChunkHeader::RIFFID_FMT) {
                chunk = mFmtChunk = new WavFmtChunkHeader(tag);
                mFmtChunk->read(mStream);
            } else if (tag == WavChunkHeader::RIFFID_DATA) {
                chunk = mDataChunk = new WavChunkHeader(tag);
                mDataChunk->read(mStream);
                // We are now positioned at the start of the audio data.
                mAudioDataStartPos = mStream->getPos();
                mStream->advance(mDataChunk->mChunkSize);
            } else {
                chunk = new WavChunkHeader(tag);
                chunk->read(mStream);
                mStream->advance(mDataChunk->mChunkSize); // skip the body
            }

            (*mChunkMap)[tag] = chunk;
        }

        if (mDataChunk != nullptr) {
            mStream->setPos(mAudioDataStartPos);
        }
    }

// Data access
    void WavStreamReader::positionToAudio() {
        if (mDataChunk != nullptr) {
            mStream->setPos(mAudioDataStartPos);
        }
    }

    int WavStreamReader::getDataFloat(float *buff, int numFrames) {
        // __android_log_print(ANDROID_LOG_INFO, TAG, "getData(%d)", numFrames);

        if (mDataChunk == nullptr || mFmtChunk == nullptr) {
            return 0;
        }

        int totalFramesRead = 0;

        int numChans = mFmtChunk->mNumChannels;
        int buffOffset = 0;
        bool excludeStartMute = true;

        // TODO - Manage other input formats
        if (mFmtChunk->mSampleSize == 16) {
            auto *readBuff = new short[128 * numChans];
            int framesLeft = numFrames;
            while (framesLeft > 0) {
                int framesThisRead = std::min(framesLeft, 128);
                int numFramesRead =
                        mStream->read(readBuff, framesThisRead * sizeof(short) * numChans) /
                        (sizeof(short) * numChans);
                totalFramesRead += numFramesRead;

                // convert
                for (int offset = 0; offset < numFramesRead * numChans; offset++) {
                    short dataResult = readBuff[offset];
                    if (!excludeStartMute || std::abs(dataResult) > 0) {
                        excludeStartMute = false;
                        buff[buffOffset++] = (float) dataResult / (float) 0x7FFF;
                    }
                }

                if (numFramesRead < framesThisRead) {
                    break; // none left
                }

                framesLeft -= framesThisRead;
            }
            delete[] readBuff;

            // Zero out any unread frames
            if (buffOffset < numFrames) {
                int numChannels = getNumChannels();
                memset(buff + (buffOffset * numChannels), 0,
                       (numFrames - buffOffset) * sizeof(buff[0]) * numChannels);
            }

            // __android_log_print(ANDROID_LOG_INFO, TAG, "  returns:%d", totalFramesRead);
            return totalFramesRead;
        }
        return 0;
    }

} // namespace parselib
