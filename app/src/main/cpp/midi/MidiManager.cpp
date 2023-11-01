#include <cinttypes>
#include <jni.h>
#include <pthread.h>
#include <cstdio>
#include <unistd.h>

#include <atomic>
#include <string>
#include <map>

#include <amidi/AMidi.h>
#include <android/log.h>

static const char *TAG = "MidiManager-JNI";

typedef struct {
    pthread_t sReadThread;
    AMidiDevice *sNativeReceiveDevice;
    AMidiOutputPort *sMidiOutputPort;
    bool sReading;
} midi_device_handle_t;

// The Data Callback
static JavaVM *theJvm;           // Need this for allocating data buffer for...
static jclass dataCallbackClass;  // This is the (Java) class...
static jmethodID midDataCallback;  // ...this callback routine

static std::map<intptr_t, midi_device_handle_t> midiDeviceMap;

static void sendTheReceivedData(uint8_t *data, size_t numBytes) {
    JNIEnv *env;
    theJvm->AttachCurrentThread(&env, nullptr);
    if (env == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error retrieving JNI Env");
    }

    // send it to the (Java) callback
    for (size_t i = 0; i < numBytes; i += 3) {
        env->CallStaticVoidMethod(dataCallbackClass, midDataCallback,
                                  data[i], data[i + 1], data[i + 2]);
    }
}

/*
 * Receiving API
 */
/**
 * This routine polls the input port and dispatches received data to the
 * application-provided (Java) callback.
 */
static void *readThreadRoutine(void *context) {
    midi_device_handle_t &midiDeviceHandle = midiDeviceMap[reinterpret_cast<intptr_t>(context)];
    const size_t MAX_BYTES_TO_RECEIVE = 128;
    uint8_t incomingMessage[MAX_BYTES_TO_RECEIVE];
    while (midiDeviceHandle.sReading) {
        // AMidiOutputPort_receive is non-blocking, so let's not burn up the CPU unnecessarily
        usleep(2000);
        int32_t opcode;
        size_t numBytesReceived;
        int64_t timestamp;
        ssize_t numMessagesReceived = AMidiOutputPort_receive(
                midiDeviceHandle.sMidiOutputPort, &opcode, incomingMessage,
                MAX_BYTES_TO_RECEIVE, &numBytesReceived, &timestamp);
        if (numMessagesReceived < 0) {
            __android_log_print(ANDROID_LOG_WARN, TAG, "Failure receiving MIDI data %zd",
                                numMessagesReceived);
            // Exit the thread
            midiDeviceHandle.sReading = false;
        } else if (numMessagesReceived > 0 && numBytesReceived >= 0) {
            if (opcode == AMIDI_OPCODE_DATA && (incomingMessage[0] & 0xF0) != 0xF0) {
                sendTheReceivedData(incomingMessage, numBytesReceived);
            }
        }
    }
    return nullptr;
}

//
// JNI Functions
//
extern "C" {

/**
 * Native implementation of TBMidiManager.startReadingMidi() method.
 * Opens the first "output" port from specified MIDI device for isReading.
 * @param   env             JNI Env pointer.
 * @param   clazz           (Java) class.
 * @param   midiDeviceObj   (Java) MidiDevice object.
 * @param   portNumber      The index of the "output" port to open.
 */
void Java_ly_pp_justpiano3_utils_MidiDeviceUtil_startReadingMidi(
        JNIEnv *env, jclass clazz, jobject midiDeviceObj, jint portNumber, jint deviceId) {
    env->GetJavaVM(&theJvm);
    // Setup the receive data callback (into Java)
    dataCallbackClass = static_cast<jclass>(env->NewGlobalRef(clazz));
    midDataCallback = env->GetStaticMethodID(clazz, "onMidiMessageReceive", "(BBB)V");

    midi_device_handle_t deviceHandle;
    deviceHandle.sReading = true;
    AMidiDevice_fromJava(env, midiDeviceObj, &deviceHandle.sNativeReceiveDevice);
    AMidiOutputPort_open(deviceHandle.sNativeReceiveDevice, portNumber,
                         &deviceHandle.sMidiOutputPort);
    // Start read thread
    pthread_create(&deviceHandle.sReadThread, nullptr, readThreadRoutine,
                   reinterpret_cast<void *>(static_cast<intptr_t>(deviceId)));
    // store value by portNumber to support multi-device open
    midiDeviceMap[static_cast<intptr_t>(deviceId)] = deviceHandle;
}

/**
 * Native implementation of the (Java) TBMidiManager.stopReadingMidi() method.
 * @param   (unnamed)   JNI Env pointer.
 * @param   (unnamed)   TBMidiManager (Java) object.
 */
void Java_ly_pp_justpiano3_utils_MidiDeviceUtil_stopReadingMidi(JNIEnv *, jclass, jint deviceId) {
    midi_device_handle_t &midiDeviceHandle = midiDeviceMap[static_cast<intptr_t>(deviceId)];
    AMidiOutputPort_close(midiDeviceHandle.sMidiOutputPort);
    // need some synchronization here
    midiDeviceHandle.sReading = false;
    pthread_join(midiDeviceHandle.sReadThread, nullptr);
    AMidiDevice_release(midiDeviceHandle.sNativeReceiveDevice);
    midiDeviceMap.erase(static_cast<intptr_t>(deviceId));
}
}  // extern "C"
