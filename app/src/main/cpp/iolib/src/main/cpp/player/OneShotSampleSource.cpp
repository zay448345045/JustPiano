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

    void OneShotSampleSource::mixAudio(float *outBuff, int32_t numChannels, int32_t delay,
                                       int32_t numFrames,
                                       std::tuple<int32_t, float, bool> *curFrameIndex) {
        int32_t numSampleFrames = mSampleBuffer->getNumSampleFrames();
        std::tuple<int32_t, float, bool>& tuple = *curFrameIndex;
        int32_t trueIndex = get<0>(tuple);
        float trueVolume = get<1>(tuple);
        bool isStop = get<2>(tuple);
        int32_t numWriteFrames = !mCurFrameIndexVector->empty()
                                 ? std::min(numFrames, numSampleFrames - trueIndex)
                                 : 0;
        if (numWriteFrames != 0 && trueIndex < numSampleFrames && trueVolume > 0) {
            // Mix in the samples
            // investigate unrolling these loops...
            const float *data = mSampleBuffer->getSampleData();
            int32_t sampleCount = numWriteFrames * numChannels;
            for (int32_t i = 0; i < sampleCount; i += numChannels) {
                if (isStop) {
                    trueVolume -= 0.002f / ((float) delay * 3.0f + 50);
                }
                outBuff[i] += data[trueIndex++] * trueVolume;
                outBuff[i + 1] = outBuff[i];
            }
        }
        get<0>(tuple) = trueIndex;
        get<1>(tuple) = trueVolume;
        // silence
        // no need as the output buffer would need to have been filled with silence
        // to be mixed into
    }

} // namespace iolib
