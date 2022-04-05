/*
 * Copyright (C) 2020 The Android Open Source Project
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

#ifndef _PLAYER_SAMPLESOURCE_
#define _PLAYER_SAMPLESOURCE_

#include <cstdint>
#include <queue>

#include "DataSource.h"

#include "SampleBuffer.h"

namespace iolib {

/**
 * Defines an interface for audio data provided to a player object.
 * Concrete examples include OneShotSampleBuffer. One could imagine a LoopingSampleBuffer.
 * Supports stereo position via mPan member.
 */
    class SampleSource : public DataSource {
    public:

        SampleSource(SampleBuffer *sampleBuffer)
                : mSampleBuffer(sampleBuffer), mGain(1.0f) {}

        virtual ~SampleSource() {}

        void setPlayMode(int32_t volume) {
            mCurFrameIndexQueue.push(std::make_pair(0, volume));
        }

        void setStopMode() {
            while (!mCurFrameIndexQueue.empty()) {
                mCurFrameIndexQueue.pop();
            }
        }

        void setGain(float gain) {
            mGain = gain;
        }

        float getGain() const {
            return mGain;
        }

        int32_t getCurFrameIndexQueueSize() {
            return mCurFrameIndexQueue.size();
        }

        std::pair<int32_t, int32_t> &frontCurFrameIndexQueue() {
            return mCurFrameIndexQueue.front();
        }

        void pushCurFrameIndexQueue(std::pair<int32_t, int32_t> &pair) {
            mCurFrameIndexQueue.push(pair);
        }

        void popCurFrameIndexQueue() {
            mCurFrameIndexQueue.pop();
        }

    protected:
        SampleBuffer *mSampleBuffer;

        std::queue<std::pair<int32_t, int32_t>> mCurFrameIndexQueue;

        // Overall gain
        float mGain;
    };

} // namespace wavlib

#endif //_PLAYER_SAMPLESOURCE_
