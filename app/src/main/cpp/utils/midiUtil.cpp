#include <jni.h>

extern "C" {

// Data callback stuff
JavaVM* theJvm;
jclass dataCallbackClass;
jmethodID midDataCallback;

/**
 * Initializes JNI interface stuff, specifically the info needed to call back
 * into the Java layer when MIDI data is received.
 */
JNICALL void Java_ly_pp_justpiano3_utils_MidiUtil_initNative(
    JNIEnv* env, jclass cls) {
  env->GetJavaVM(&theJvm);

  // Setup the receive data callback (into Java)
  jclass clsMainActivity =
      env->FindClass("ly/pp/justpiano3/utils/MidiUtil");
  dataCallbackClass = static_cast<jclass>(env->NewGlobalRef(cls));
  midDataCallback =
      env->GetMethodID(clsMainActivity, "onNativeMessageReceive", "([B)V");
}

}  // extern "C"
