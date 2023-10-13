#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "fluidsynth.h"
#include "sf2_synth_engine.h"

#include <android/log.h>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "TEST", __VA_ARGS__)

typedef struct {
    fluid_settings_t *settings;
    fluid_synth_t *synth;
    int soundfont_id;
} fluid_handle_t;

typedef struct {
    JNIEnv *env;
    jobject options;
} fluid_settings_foreach_option_data;

void fluid_settings_foreach_option_callback(void *data, char *name, char *option) {
    fluid_settings_foreach_option_data *handle = (fluid_settings_foreach_option_data *) data;

    jstring joption = (*handle->env)->NewStringUTF(handle->env, option);
    jclass cl = (*handle->env)->GetObjectClass(handle->env, handle->options);
    jmethodID mid = (*handle->env)->GetMethodID(handle->env, cl, "add", "(Ljava/lang/Object;)Z");
    if (mid != 0) {
        (*handle->env)->CallBooleanMethod(handle->env, handle->options, mid, joption);
    }
}

JNIEXPORT jlong JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_malloc(JNIEnv *env, jclass obj) {
    jlong ptr = 0;

    fluid_handle_t *handle = (fluid_handle_t *) malloc(sizeof(fluid_handle_t));

    handle->settings = new_fluid_settings();
    handle->synth = NULL;
    handle->soundfont_id = 0;

    fluid_settings_setint(handle->settings, "synth.audio-channels", 2);
    fluid_settings_setint(handle->settings, "synth.cpu-cores", 4);
    fluid_settings_setint(handle->settings, "audio.periods", 64);
    fluid_settings_setint(handle->settings, "audio.period-size", 8192);
    fluid_settings_setint(handle->settings, "audio.realtime-prio", 99);
    fluid_settings_setstr(handle->settings, "player.timing-source", "system");

    memcpy(&ptr, &handle, sizeof(handle));

    return ptr;
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_free(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL) {
        if (handle->synth != NULL) {
            delete_fluid_synth(handle->synth);
        }
        if (handle->settings != NULL) {
            delete_fluid_settings(handle->settings);
        }
        free(handle);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_open(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->settings != NULL) {
        if (handle->synth != NULL) {
            delete_fluid_synth(handle->synth);
        }
        handle->synth = new_fluid_synth(handle->settings);

        fluid_synth_set_interp_method(handle->synth, -1, FLUID_INTERP_NONE);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_close(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL) {

        if (handle->synth != NULL) {
            delete_fluid_synth(handle->synth);
        }
        handle->synth = NULL;
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_loadFont(JNIEnv *env, jclass obj, jlong ptr,
                                                             jstring path) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL && handle->soundfont_id <= 0) {
        const jbyte *font = (*env)->GetStringUTFChars(env, path, NULL);
        handle->soundfont_id = fluid_synth_sfload(handle->synth, font, 1);
        (*env)->ReleaseStringUTFChars(env, path, font);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_unloadFont(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL && handle->soundfont_id > 0) {
        fluid_synth_sfunload(handle->synth, handle->soundfont_id, 1);
        handle->soundfont_id = 0;
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_systemReset(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_system_reset(handle->synth);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_noteOn(JNIEnv *env, jclass obj, jlong ptr, jint channel,
                                                           jint note, jint velocity) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_noteon(handle->synth, channel, note, velocity);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_noteOff(JNIEnv *env, jclass obj, jlong ptr, jint channel,
                                                            jint note, jint velocity) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_noteoff(handle->synth, channel, note);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_programChange(JNIEnv *env, jclass obj, jlong ptr,
                                                                  jint channel, jint program) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_program_change(handle->synth, channel, program);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_controlChange(JNIEnv *env, jclass obj, jlong ptr,
                                                                  jint channel, jint control, jint value) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_cc(handle->synth, channel, control, value);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_pitchBend(JNIEnv *env, jclass obj, jlong ptr,
                                                              jint channel, jint value) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        fluid_synth_pitch_bend(handle->synth, channel, ((value * 128)));
    }
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthSoundEngineUtil_fillBuffer
        (JNIEnv *env, jclass obj, jlong ptr, jshortArray shortarray, jint len) {
    fluid_handle_t *handle = NULL;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != NULL && handle->synth != NULL) {
        if (!shortarray) {
            return;
        }
        jshort *arr;
        arr = (*env)->GetShortArrayElements(env, shortarray, NULL);
        if (!arr) {
            return;
        }
        fluid_synth_write_s16(handle->synth, len / 2, arr, 0, 2, arr, 1, 2);

        (*env)->ReleaseShortArrayElements(env, shortarray, arr, 0);
    }
}