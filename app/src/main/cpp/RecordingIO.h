//
// Created by asalehin on 7/11/20.
//

#ifndef FAST_MIXER_RECORDINGIO_H
#define FAST_MIXER_RECORDINGIO_H

#include "sndfile.hh"
#include "taskqueue/TaskQueue.h"
#include "oboe/Definitions.h"

#ifndef MODULE_NAME
#define MODULE_NAME  "RecordingIO"
#endif

using namespace std;


class RecordingIO {
public:

    RecordingIO();

    ~RecordingIO() {
        taskQueue->stop_queue();
    }

    void init(int32_t sampleRate, int32_t channelCount);

    int32_t write(const float *sourceData, int32_t numSamples);

    void flush_buffer();

    void setRecordingFilePath(string recordingFilePath) {
        mRecordingFilePath = move(recordingFilePath);
    }

    void reserveRecordingBuffer(int reserve);

    void clearRecordingBuffer();

private:
    TaskQueue *taskQueue;

    string mRecordingFilePath;

    int32_t mSampleRate;
    int32_t mChannelCount;

    shared_ptr<SndfileHandle> mRecordingFile {nullptr};

    vector<float> mData;
    float* mBuff{};

    static void flush_to_file(float* data, int32_t length, int32_t sampleRate, const string& recordingFilePath, shared_ptr<SndfileHandle>& recordingFile);

    static mutex flushMtx;
    static condition_variable flushed;
    static bool ongoing_flush_completed;
    static bool check_if_flush_completed();
};


#endif //FAST_MIXER_RECORDINGIO_H
