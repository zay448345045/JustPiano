//
// Created by asalehin on 7/11/20.
//

#include <cstdint>
#include <cstdio>
#include <string>
#include <unistd.h>
#include "RecordingIO.h"
#include "utils/Utils.h"
#include <mutex>
#include <condition_variable>
#include <sys/stat.h>

mutex RecordingIO::flushMtx;
condition_variable RecordingIO::flushed;
bool RecordingIO::ongoing_flush_completed = true;

bool RecordingIO::check_if_flush_completed() {
    return ongoing_flush_completed;
}

RecordingIO::RecordingIO() : mSampleRate(0), mChannelCount(0) {
    taskQueue = new TaskQueue();
}

void RecordingIO::reserveRecordingBuffer(int reserve) {
    mData.reserve(reserve);
}

void RecordingIO::clearRecordingBuffer() {
    mData.resize(0);
    taskQueue->clear_queue();
    mRecordingFile.reset();
}

void RecordingIO::flush_to_file(float *data, int32_t length, int32_t sampleRate,
                                const string &recordingFilePath,
                                shared_ptr<SndfileHandle> &recordingFile) {
    if (!recordingFile) {
        int format = SF_FORMAT_WAV | SF_FORMAT_FLOAT;
        SndfileHandle file = SndfileHandle(recordingFilePath, SFM_WRITE, format,
                                           oboe::ChannelCount::Mono, sampleRate);
        recordingFile = make_shared<SndfileHandle>(file);
    }
    recordingFile->write(data, length);
}

int32_t RecordingIO::write(const float *sourceData, int32_t numSamples) {
    // Wait if a flush action is already in progress
    unique_lock<mutex> lck(flushMtx);
    flushed.wait(lck, check_if_flush_completed);
    lck.unlock();

    int size = numSamples * mChannelCount;
    if (mData.size() + size >= mSampleRate) {
        flush_buffer();
    }
    for (int i = 0; i < size; i += mChannelCount) {
        mData.push_back(sourceData[i]);
    }
    return numSamples;
}

void RecordingIO::flush_buffer() {
    if (!mData.empty()) {
        ongoing_flush_completed = false;
        int numItems = mData.size();
        copy(mData.begin(), mData.begin() + numItems, mBuff);
        taskQueue->enqueue([&]() {
            flush_to_file(mBuff, numItems, mSampleRate, mRecordingFilePath, mRecordingFile);
        });
        mData.clear();
        ongoing_flush_completed = true;
        flushed.notify_all();
    }
}

void RecordingIO::init(int32_t sampleRate, int32_t channelCount) {
    mBuff = new float[sampleRate];
    mSampleRate = sampleRate;
    mChannelCount = channelCount;
}


