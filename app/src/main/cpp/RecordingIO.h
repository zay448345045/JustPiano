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
    int32_t write(const float *sourceData, int32_t numSamples);

    void flush_buffer();

    void setRecordingFilePath(string recordingFilePath) {
        mRecordingFilePath = move(recordingFilePath);
    }

    void resetProperties();

    void reserveRecordingBuffer();

    void clearRecordingBuffer();

private:
    TaskQueue *taskQueue;

    string mRecordingFilePath;

    shared_ptr<SndfileHandle> mRecordingFile {nullptr};

    vector<float> mData;
    int kMaxSamples = oboe::DefaultStreamValues::SampleRate * oboe::ChannelCount::Mono;
    float* mBuff = new float[kMaxSamples]{0};

    static void flush_to_file(float* data, int32_t length, const string& recordingFilePath, shared_ptr<SndfileHandle>& recordingFile);

    static mutex flushMtx;
    static condition_variable flushed;
    static bool ongoing_flush_completed;
    static bool check_if_flush_completed();
};


#endif //FAST_MIXER_RECORDINGIO_H
