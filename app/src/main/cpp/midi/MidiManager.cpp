#include <cinttypes>
#include <jni.h>
#include <pthread.h>
#include <cstdio>
#include <unistd.h>

#include <atomic>
#include <string>
#include <chrono>

#include <amidi/AMidi.h>
#include <android/log.h>

static const char *TAG = "MidiManager-JNI";
static AMidiDevice *sNativeReceiveDevice = nullptr;
// The thread only reads this value, so no special protection is required.
static AMidiOutputPort *sMidiOutputPort = nullptr;

static pthread_t sReadThread;
static std::atomic<bool> isReading(false);

// The Data Callback
static JavaVM *theJvm;           // Need this for allocating data buffer for...
static jclass dataCallbackClass;  // This is the (Java) class...
static jmethodID midDataCallback;  // ...this callback routine

static void sendTheReceivedData(uint8_t *data, int numBytes) {
    JNIEnv *env;
    theJvm->AttachCurrentThread(&env, nullptr);
    if (env == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error retrieving JNI Env");
    }

    // send it to the (Java) callback
    for (int i = 0; i < numBytes; i += 3) {
        if ((data[i] & 0xF0) == 0x90) {
            env->CallStaticVoidMethod(dataCallbackClass, midDataCallback, data[i + 1], data[i + 2]);
        } else if ((data[i] & 0xF0) == 0x80) {
            env->CallStaticVoidMethod(dataCallbackClass, midDataCallback, data[i + 1], 0);
        }
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
    (void) context;  // unused
    isReading = true;
    AMidiOutputPort *outputPort = sMidiOutputPort;

    const size_t MAX_BYTES_TO_RECEIVE = 128;
    uint8_t incomingMessage[MAX_BYTES_TO_RECEIVE];

    while (isReading) {
        // AMidiOutputPort_receive is non-blocking, so let's not burn up the CPU unnecessarily
        usleep(2000);
        int32_t opcode;
        size_t numBytesReceived;
        int64_t timestamp;
        ssize_t numMessagesReceived = AMidiOutputPort_receive(
                outputPort, &opcode, incomingMessage, MAX_BYTES_TO_RECEIVE,
                &numBytesReceived, &timestamp);

        if (numMessagesReceived < 0) {
            __android_log_print(ANDROID_LOG_WARN, TAG, "Failure receiving MIDI data %zd",
                                numMessagesReceived);
            // Exit the thread
            isReading = false;
        } else if (numMessagesReceived > 0 && numBytesReceived >= 0) {
            if (opcode == AMIDI_OPCODE_DATA && (incomingMessage[0] & 0xF0) != 0xF0) {
                sendTheReceivedData(incomingMessage, numBytesReceived);
            }
        }
    }  // end while(isReading)
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
void Java_ly_pp_justpiano3_utils_MidiUtil_startReadingMidi(
        JNIEnv *env, jclass clazz, jobject midiDeviceObj, jint portNumber) {
    env->GetJavaVM(&theJvm);
    // Setup the receive data callback (into Java)
    dataCallbackClass = static_cast<jclass>(env->NewGlobalRef(clazz));
    midDataCallback = env->GetStaticMethodID(clazz, "onNativeMessageReceive", "(BB)V");

    AMidiDevice_fromJava(env, midiDeviceObj, &sNativeReceiveDevice);
    AMidiOutputPort *outputPort;
    AMidiOutputPort_open(sNativeReceiveDevice, portNumber, &outputPort);

    // sMidiOutputPort.store(outputPort);
    sMidiOutputPort = outputPort;

    // Start read thread
    pthread_create(&sReadThread, nullptr, readThreadRoutine, nullptr);
}

/**
 * Native implementation of the (Java) TBMidiManager.stopReadingMidi() method.
 * @param   (unnamed)   JNI Env pointer.
 * @param   (unnamed)   TBMidiManager (Java) object.
 */
void Java_ly_pp_justpiano3_utils_MidiUtil_stopReadingMidi(JNIEnv *, jclass) {
    // need some synchronization here
    isReading = false;
    pthread_join(sReadThread, nullptr);

    AMidiDevice_release(sNativeReceiveDevice);
    sNativeReceiveDevice = nullptr;
}
}  // extern "C"
