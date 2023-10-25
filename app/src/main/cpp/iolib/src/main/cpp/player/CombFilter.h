#ifndef _PLAYER_COMB_FILTER_
#define _PLAYER_COMB_FILTER_

#include <vector>

namespace iolib {

    class CombFilter {
    private:
        std::vector<float> mBuffer;
        int mPointer;
        float mGain;
        int mDelay;

    public:
        CombFilter(float gain, int delay) {
            this->mGain = gain;
            this->mDelay = delay;
            this->mPointer = 0;
            this->mBuffer.resize(delay);
            std::fill(mBuffer.begin(), mBuffer.end(), 0.0f);
        }

        float process(float input) {
            if (mDelay == 0) {
                return input;
            }
            float output = mBuffer[mPointer];
            mBuffer[mPointer] = input + mBuffer[mPointer] * mGain;
            mPointer = (mPointer + 1) % mDelay;
            return output;
        }

        void setGain(float gain) {
            this->mGain = gain;
        }

        void setDelay(int delay) {
            this->mDelay = delay;
            this->mBuffer.resize(delay);
            std::fill(mBuffer.begin(), mBuffer.end(), 0.0f);
        }
    };

} // namespace iolib

#endif //_PLAYER_COMB_FILTER_
