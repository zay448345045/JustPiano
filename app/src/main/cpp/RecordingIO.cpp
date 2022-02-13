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

RecordingIO::RecordingIO() {
    taskQueue = new TaskQueue();
}

void RecordingIO::reserveRecordingBuffer() {
    mData.reserve(kMaxSamples);
}

void RecordingIO::clearRecordingBuffer() {
    mData.resize(0);
}

void RecordingIO::flush_to_file(float* data, int32_t length, const string& recordingFilePath, shared_ptr<SndfileHandle>& recordingFile) {
    if (!recordingFile) {
        int format = SF_FORMAT_WAV | SF_FORMAT_FLOAT;
        SndfileHandle file = SndfileHandle(recordingFilePath, SFM_WRITE, format, oboe::ChannelCount::Mono, 44100);
        recordingFile = make_shared<SndfileHandle>(file);
    }

    recordingFile->write(data, length);
}

int32_t RecordingIO::write(const float *sourceData, int32_t numSamples) {
    // Wait if a flush action is already in progress
    // Known Issue: If live playback is on, this will blink in the live playback for a while

    unique_lock<mutex> lck(flushMtx);
    flushed.wait(lck, check_if_flush_completed);
    lck.unlock();

    if (mData.size() + numSamples >= kMaxSamples) {
        flush_buffer();
    }

    for(int i = 0; i < numSamples; i++) {
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
            flush_to_file(mBuff, numItems, mRecordingFilePath, mRecordingFile);
        });

        mData.clear();
        ongoing_flush_completed = true;
        flushed.notify_all();
    }
}

void RecordingIO::resetProperties() {
    taskQueue->clear_queue();
    mRecordingFile.reset();
}


