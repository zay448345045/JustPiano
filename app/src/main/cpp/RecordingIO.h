//
// Created by asalehin on 7/11/20.
//

#ifndef FAST_MIXER_RECORDINGIO_H
#define FAST_MIXER_RECORDINGIO_H

#include "taskqueue/ThreadPool.h"
#include "oboe/Definitions.h"

#ifndef MODULE_NAME
#define MODULE_NAME  "RecordingIO"
#endif

class RecordingIO {
public:
    RecordingIO();

    ~RecordingIO() {
        delete mRecordBuff;
    }

    void init(int32_t channelCount, int32_t sampleRate);

    static void generateWavFileHeader(char *header, long totalAudioLen, long longSampleRate,
                                      int32_t channelCount);

    void write_buffer(float *sourceData, size_t numSamples);

    void setRecordingFilePath(const char *recordingFilePath) {
        if (mRecordingFilePath != nullptr) {
            free(mRecordingFilePath);
        }
        mRecordingFilePath = strdup(recordingFilePath);
    }

    void reserveRecordingBuffer(int reserve);

    void clearRecordingBuffer();

private:
    ThreadPool mThreadPool;

    char *mRecordingFilePath{};

    int32_t mSampleRate;
    int32_t mChannelCount;

    int mRecordingFile;

    std::vector<float> mRecordData;
    float *mRecordBuff{};

    static std::mutex flushMtx;
    static std::condition_variable flushed;
    static bool ongoing_flush_completed;

    static bool check_if_flush_completed();
};


#endif //FAST_MIXER_RECORDINGIO_H
