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

#include <cstring>

#include "wav/WavStreamReader.h"

#include "OneShotSampleSource.h"

namespace iolib {

    void OneShotSampleSource::mixAudio(float *outBuff, int numChannels, int32_t numFrames, std::pair<int32_t, int32_t>& curFrameIndex) {
        int32_t numSampleFrames = mSampleBuffer->getNumSampleFrames();
        int32_t& trueIndex = curFrameIndex.first;
        auto trueVolume = (float) curFrameIndex.second;
        int32_t numWriteFrames = !mCurFrameIndexQueue.empty()
                                 ? std::min(numFrames, numSampleFrames - trueIndex)
                                 : 0;
        if (numWriteFrames != 0) {
            // Mix in the samples
            // investigate unrolling these loops...
            const float *data = mSampleBuffer->getSampleData();
            if (numChannels == 1) {
                // MONO output
                // do not use, because of clipping wave.
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    outBuff[frameIndex] += data[trueIndex++] * trueVolume / 64;
                }
            } else if (numChannels == 2) {
                // STEREO output
                int dstSampleIndex = 0;
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    float value = data[trueIndex] * trueVolume / 128;
                    outBuff[dstSampleIndex++] += value;
                    outBuff[dstSampleIndex++] += value;
                    trueIndex++;
                }
            } else if (numChannels == 4) {
                int dstSampleIndex = 0;
                for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                    float value = data[trueIndex] * trueVolume / 256;
                    outBuff[dstSampleIndex++] += value;
                    outBuff[dstSampleIndex++] += value;
                    outBuff[dstSampleIndex++] += value;
                    outBuff[dstSampleIndex++] += value;
                    trueIndex++;
                }
            }
        }

        // silence
        // no need as the output buffer would need to have been filled with silence
        // to be mixed into
    }

} // namespace wavlib
