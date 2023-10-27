#ifndef _PLAYER_ALL_PASS_FILTER_
#define _PLAYER_ALL_PASS_FILTER_

#include <vector>

namespace iolib {

    class AllPassFilter {
    private:
        std::vector<float> mBuffer;
        int mPointer;
        float mGain;
        int mDelay;

    public:
        AllPassFilter(float gain, int delay) {
            this->mGain = gain;
            this->mPointer = 0;
            setDelay(delay);
        }

        float process(float input) {
            if (mDelay == 0) {
                return input;
            }
            float output = mBuffer[mPointer] - input * mGain;
            mBuffer[mPointer] = input + output * mGain;
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

#endif //_PLAYER_ALL_PASS_FILTER_
