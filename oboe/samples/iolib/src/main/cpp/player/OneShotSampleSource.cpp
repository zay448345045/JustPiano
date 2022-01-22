/*
 * Copyright (C) 2019 The Android Open Source Project
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

#include <string.h>

#include "wav/WavStreamReader.h"

#include "OneShotSampleSource.h"

namespace iolib {

    void OneShotSampleSource::mixAudio(float *outBuff, int numChannels, int32_t numFrames, int32_t& curFrameIndex) {
        int32_t numSampleFrames = mSampleBuffer->getNumSampleFrames();
        int32_t numWriteFrames = !mCurFrameIndexQueue.empty()
                                 ? std::min(numFrames, numSampleFrames - curFrameIndex)
                                 : 0;

        if (numWriteFrames != 0) {
            // Mix in the samples

            // investigate unrolling these loops...
            const float *data = mSampleBuffer->getSampleData();
            if (numChannels == 1) {
                // MONO output
                // stop to use, because it can clipping wave.
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    outBuff[frameIndex] += data[curFrameIndex++] * mGain;
                }
            } else if (numChannels == 2) {
                // STEREO output
                int dstSampleIndex = 0;
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * mLeftGain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex++] * mRightGain;
                }
            } else if (numChannels == 3) {
                int dstSampleIndex = 0;
                float gain = mGain / 3;
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * gain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * gain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex++] * gain;
                }
            } else if (numChannels == 4) {
                int dstSampleIndex = 0;
                float gain = mGain / 4;
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * gain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * gain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex] * gain;
                    outBuff[dstSampleIndex++] += data[curFrameIndex++] * gain;
                }
            }
        }

        // silence
        // no need as the output buffer would need to have been filled with silence
        // to be mixed into
    }

} // namespace wavlib
