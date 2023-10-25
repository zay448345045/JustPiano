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

#include <vector>

#include "DataSource.h"
#include "SampleBuffer.h"

using namespace std;

namespace iolib {

/**
 * Defines an interface for audio data provided to a player object.
 * Concrete examples include OneShotSampleBuffer. One could imagine a LoopingSampleBuffer.
 * Supports stereo position via mPan member.
 */
    class SampleSource : public DataSource {
    public:

        SampleSource(SampleBuffer *sampleBuffer) : mSampleBuffer(sampleBuffer) {}

        virtual ~SampleSource() {}

        void setPlayMode(int32_t volume) {
            mCurFrameIndexVector->push_back(
                    tuple<int32_t, float, bool>(0, (float) volume / 128.0f, false));
        }

        void setStopMode() {
            for (auto &curTuple: *mCurFrameIndexVector) {
                get<2>(curTuple) = true;
            }
        }

        shared_ptr <vector<tuple < int32_t, float, bool>>>

        getCurFrameIndexVector() {
            return mCurFrameIndexVector;
        }

    protected:
        SampleBuffer *mSampleBuffer;

        shared_ptr <vector<tuple < int32_t, float, bool>>> mCurFrameIndexVector
        { make_shared < vector < tuple < int32_t, float, bool>>>() };
    };

} // namespace iolib

#endif //_PLAYER_SAMPLESOURCE_
