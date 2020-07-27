/*
 * Copyright 2019 The Android Open Source Project
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

#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>

#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>

static const char *TAG = "SoundEngine";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer sDTPlayer;

/**
 * Native (JNI) implementation of DrumPlayer.setupAudioStreamNative()
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_JPApplication_setupAudioStreamNative(
        JNIEnv *env, jclass, jint numChannels, jint sampleRate) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");

    // we know in this case that the sample buffers are all 1-channel, 41K
    sDTPlayer.setupAudioStream(numChannels, sampleRate);
}

/**
 * Native (JNI) implementation of DrumPlayer.teardownAudioStreamNative()
 */
JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_JPApplication_teardownAudioStreamNative(JNIEnv *, jclass) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "deinit()");

    // we know in this case that the sample buffers are all 1-channel, 44.1K
    sDTPlayer.teardownAudioStream();
}

/**
 * Native (JNI) implementation of DrumPlayer.allocSampleDataNative()
 */
/**
 * Native (JNI) implementation of DrumPlayer.loadWavAssetNative()
 */
JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_JPApplication_loadWavAssetNative(JNIEnv *env, jclass, jbyteArray bytearray,
                                                       jint index, jfloat pan) {
    int len = env->GetArrayLength(bytearray);

    unsigned char *buf = new unsigned char[len];
    env->GetByteArrayRegion(bytearray, 0, len, reinterpret_cast<jbyte *>(buf));

    MemInputStream stream(buf, len);

    WavStreamReader reader(&stream);
    reader.parse();

    SampleBuffer *sampleBuffer = new SampleBuffer();
    sampleBuffer->loadSampleData(&reader);

    OneShotSampleSource *source = new OneShotSampleSource(sampleBuffer, pan);
    sDTPlayer.addSampleSource(source, sampleBuffer);

    delete[] buf;
}

/**
 * Native (JNI) implementation of DrumPlayer.unloadWavAssetsNative()
 */
JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_JPApplication_unloadWavAssetsNative(JNIEnv *env, jclass) {
    sDTPlayer.unloadSampleData();
}

/**
 * Native (JNI) implementation of DrumPlayer.trigger()
 */
JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_JPApplication_trigger(JNIEnv *env, jclass, jint index) {
    sDTPlayer.triggerDown(index);
}

/**
 * Native (JNI) implementation of DrumPlayer.getOutputReset()
 */
JNIEXPORT jboolean JNICALL Java_ly_pp_justpiano3_JPApplication_getOutputReset(JNIEnv *, jclass) {
    return sDTPlayer.getOutputReset();
}

/**
 * Native (JNI) implementation of DrumPlayer.clearOutputReset()
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_JPApplication_clearOutputReset(JNIEnv *, jclass) {
    sDTPlayer.clearOutputReset();
}

/**
 * Native (JNI) implementation of DrumPlayer.restartStream()
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_JPApplication_restartStream(JNIEnv *, jclass) {
    sDTPlayer.resetAll();
    if (sDTPlayer.openStream()) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_JPApplication_setPan(
        JNIEnv *env, jclass thiz, jint index, jfloat pan) {
    sDTPlayer.setPan(index, pan);
}

JNIEXPORT jfloat JNICALL Java_ly_pp_justpiano3_JPApplication_getPan(
        JNIEnv *env, jclass thiz, jint index) {
    return sDTPlayer.getPan(index);
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_JPApplication_setGain(
        JNIEnv *env, jclass thiz, jint index, jfloat gain) {
    sDTPlayer.setGain(index, gain);
}

JNIEXPORT jfloat JNICALL Java_ly_pp_justpiano3_JPApplication_getGain(
        JNIEnv *env, jclass thiz, jint index) {
    return sDTPlayer.getGain(index);
}

#ifdef __cplusplus
}
#endif
