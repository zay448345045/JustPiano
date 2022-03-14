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
#include <cstdlib>
#include <fcntl.h>
#include <condition_variable>
#include <sys/stat.h>

mutex RecordingIO::flushMtx;
condition_variable RecordingIO::flushed;
bool RecordingIO::ongoing_flush_completed = true;

bool RecordingIO::check_if_flush_completed() {
    return ongoing_flush_completed;
}

RecordingIO::RecordingIO() : pool(5), mSampleRate(0), mChannelCount(0), mRecordingFile(-1) {}

void RecordingIO::reserveRecordingBuffer(int reserve) {
    mData.reserve(reserve);
}

void RecordingIO::clearRecordingBuffer() {
    mData.resize(0);
    if (mRecordingFile != -1) {
        int file_size = lseek(mRecordingFile, 0L, SEEK_END);
        lseek(mRecordingFile, 0L, SEEK_SET);
        int wavFilePathLength = strlen(mRecordingFilePath);
        mRecordingFilePath[wavFilePathLength - 3] = 'w';
        mRecordingFilePath[wavFilePathLength - 2] = 'a';
        mRecordingFilePath[wavFilePathLength - 1] = 'v';
        int wavFile = open(mRecordingFilePath, O_CREAT | O_APPEND | O_WRONLY, S_IRWXO | S_IRWXG | S_IRWXU);
        if (wavFile != -1) {
            int buffer_size = 8192;
            char buffer[buffer_size];
            int nRead;
            char header[44];
            generateWavFileHeader(header, file_size, mSampleRate, 1, 32);
            write(wavFile, header, 44);
            while ((nRead = read(mRecordingFile, buffer, buffer_size)) > 0) {
                write(wavFile, buffer, nRead);
                bzero(buffer, buffer_size);
            }
            close(wavFile);
        }
        close(mRecordingFile);
        mRecordingFilePath[wavFilePathLength - 3] = 'r';
        mRecordingFilePath[wavFilePathLength - 2] = 'a';
        mRecordingFilePath[wavFilePathLength - 1] = 'w';
        remove(mRecordingFilePath);
    }
    mRecordingFile = -1;
}

void RecordingIO::flush_to_file(float *data, int32_t length, int32_t sampleRate,
                                char *recordingFilePath,
                                int &recordingFile) {
    if (recordingFile == -1) {
        recordingFile = open(recordingFilePath, O_CREAT | O_APPEND | O_RDWR, S_IRWXU);
    }
    if (recordingFile != -1) {
        write(recordingFile, data, length * sizeof(float));
    }
}

int32_t RecordingIO::write_buffer(const float *sourceData, int32_t numSamples) {
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
        pool.enqueue([&]() {
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


void RecordingIO::generateWavFileHeader(char* header, long totalAudioLen, long longSampleRate, int channels, int audioFormat) {
    long totalDataLen = totalAudioLen + 36;
    long byteRate = longSampleRate * 2 * channels;
    header[0] = 'R'; // RIFF
    header[1] = 'I';
    header[2] = 'F';
    header[3] = 'F';
    //文件长度  4字节文件长度，这个长度不包括"RIFF"标志(4字节)和文件长度本身所占字节(4字节),即该长度等于整个文件长度 - 8
    header[4] = (char) (totalDataLen & 0xff);
    header[5] = (char) ((totalDataLen >> 8) & 0xff);
    header[6] = (char) ((totalDataLen >> 16) & 0xff);
    header[7] = (char) ((totalDataLen >> 24) & 0xff);
    //fcc type：4字节 "WAVE" 类型块标识, 大写
    header[8] = 'W';
    header[9] = 'A';
    header[10] = 'V';
    header[11] = 'E';
    //FMT Chunk   4字节 表示"fmt" chunk的开始,此块中包括文件内部格式信息，小写, 最后一个字符是空格
    header[12] = 'f'; // 'fmt '
    header[13] = 'm';
    header[14] = 't';
    header[15] = ' ';//过渡字节
    //数据大小  4字节，文件内部格式信息数据的大小，过滤字节（一般为00000010H）
    header[16] = 16;
    header[17] = 0;
    header[18] = 0;
    header[19] = 0;
    //编码方式 10H为PCM编码格式   FormatTag：2字节，音频数据的编码方式，1：表示是PCM 编码
    header[20] = 3;
    header[21] = 0;
    //通道数  Channels：2字节，声道数，单声道为1，双声道为2
    header[22] = (char) channels;
    header[23] = 0;
    //采样率，每个通道的播放速度
    header[24] = (char) (longSampleRate & 0xff);
    header[25] = (char) ((longSampleRate >> 8) & 0xff);
    header[26] = (char) ((longSampleRate >> 16) & 0xff);
    header[27] = (char) ((longSampleRate >> 24) & 0xff);
    //音频数据传送速率,采样率*通道数*采样深度/8
    //4字节，音频数据传送速率, 单位是字节。其值为采样率×每次采样大小。播放软件利用此值可以估计缓冲区的大小
    //byteRate = sampleRate * (bitsPerSample / 8) * channels
    header[28] = (char) (byteRate & 0xff);
    header[29] = (char) ((byteRate >> 8) & 0xff);
    header[30] = (char) ((byteRate >> 16) & 0xff);
    header[31] = (char) ((byteRate >> 24) & 0xff);
    // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
    header[32] = (char) (2 * channels);
    header[33] = 0;
    //每个样本的数据位数
    //2字节，每个声道的采样精度; 譬如 16bit 在这里的值就是16。如果有多个声道，则每个声道的采样精度大小都一样的；
    header[34] = (char) audioFormat;
    header[35] = 0;
    //Data chunk
    //ckid：4字节，数据标志符（data），表示 "data" chunk的开始。此块中包含音频数据，小写；
    header[36] = 'd';
    header[37] = 'a';
    header[38] = 't';
    header[39] = 'a';
    //音频数据的长度，4字节，audioDataLen = totalDataLen - 36 = fileLenIncludeHeader - 44
    header[40] = (char) (totalAudioLen & 0xff);
    header[41] = (char) ((totalAudioLen >> 8) & 0xff);
    header[42] = (char) ((totalAudioLen >> 16) & 0xff);
    header[43] = (char) ((totalAudioLen >> 24) & 0xff);
}