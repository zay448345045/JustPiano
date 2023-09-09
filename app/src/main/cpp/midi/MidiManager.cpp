#include <cinttypes>
#include <jni.h>
#include <pthread.h>
#include <cstdio>
#include <unistd.h>

#include <atomic>
#include <string>

#define LOG_TAG "AppMidiManager-JNI"

#include <amidi/AMidi.h>

#include "utils/AndroidDebug.h"
#include "MidiSpec.h"

static AMidiDevice *sNativeReceiveDevice = nullptr;
// The thread only reads this value, so no special protection is required.
static AMidiOutputPort *sMidiOutputPort = nullptr;

static pthread_t sReadThread;
static std::atomic<bool> isReading(false);

// The Data Callback
static JavaVM *theJvm;           // Need this for allocating data buffer for...
static jclass dataCallbackClass;  // This is the (Java) class...
static jmethodID midDataCallback;  // ...this callback routine

static void SendTheReceivedData(uint8_t *data, int numBytes) {
    JNIEnv *env;
    theJvm->AttachCurrentThread(&env, nullptr);
    if (env == nullptr) {
        LOGE("Error retrieving JNI Env");
    }

    // Allocate the Java array and fill with received data
    jbyteArray ret = env->NewByteArray(numBytes);
    env->SetByteArrayRegion(ret, 0, numBytes, (jbyte *) data);

    // send it to the (Java) callback
    env->CallStaticVoidMethod(dataCallbackClass, midDataCallback, ret);
}

#if 0
// unblock this method if logging of the midi messages is required.
/**
 * Formats a midi message set and outputs to the log
 * @param   timestamp   The timestamp for when the message(s) was received
 * @param   dataBytes   The MIDI message bytes
 * @params  numDataBytew    The number of bytes in the MIDI message(s)
 */
static void logMidiBuffer(int64_t timestamp, uint8_t* dataBytes, size_t numDataBytes) {
#define DUMP_BUFFER_SIZE 1024
    char midiDumpBuffer[DUMP_BUFFER_SIZE];
    memset(midiDumpBuffer, 0, sizeof(midiDumpBuffer));
    int pos = snprintf(midiDumpBuffer, DUMP_BUFFER_SIZE,
            "%" PRIx64 " ", timestamp);
    for (uint8_t *b = dataBytes, *e = b + numDataBytes; b < e; ++b) {
        pos += snprintf(midiDumpBuffer + pos, DUMP_BUFFER_SIZE - pos,
                "%02x ", *b);
    }
    LOGD("%s", midiDumpBuffer);
}
#endif

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
    // AMidiOutputPort* outputPort = sMidiOutputPort.load();
    AMidiOutputPort *outputPort = sMidiOutputPort;

    const size_t MAX_BYTES_TO_RECEIVE = 128;
    uint8_t incomingMessage[MAX_BYTES_TO_RECEIVE];

    while (isReading) {
        // AMidiOutputPort_receive is non-blocking, so let's not burn up the CPU
        // unnecessarily
        usleep(2000);

        int32_t opcode;
        size_t numBytesReceived;
        int64_t timestamp;
        ssize_t numMessagesReceived = AMidiOutputPort_receive(
                outputPort, &opcode, incomingMessage, MAX_BYTES_TO_RECEIVE,
                &numBytesReceived, &timestamp);

        if (numMessagesReceived < 0) {
            LOGW("Failure receiving MIDI data %zd", numMessagesReceived);
            // Exit the thread
            isReading = false;
        }
        if (numMessagesReceived > 0 && numBytesReceived >= 0) {
            if (opcode == AMIDI_OPCODE_DATA &&
                (incomingMessage[0] & kMIDISysCmdChan) != kMIDISysCmdChan) {
                // (optionally) Dump to log
                // logMidiBuffer(timestamp, incomingMessage, numBytesReceived);
                SendTheReceivedData(incomingMessage, numBytesReceived);
            } else if (opcode == AMIDI_OPCODE_FLUSH) {
                // ignore
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
 * @param   env  JNI Env pointer.
 * @param   (unnamed)   TBMidiManager (Java) object.
 * @param   midiDeviceObj   (Java) MidiDevice object.
 * @param   portNumber      The index of the "output" port to open.
 */
void Java_ly_pp_justpiano3_utils_MidiUtil_startReadingMidi(
        JNIEnv *env, jclass clazz, jobject midiDeviceObj, jint portNumber) {
    AMidiDevice_fromJava(env, midiDeviceObj, &sNativeReceiveDevice);
    // int32_t deviceType = AMidiDevice_getType(sNativeReceiveDevice);
    // ssize_t numPorts = AMidiDevice_getNumOutputPorts(sNativeReceiveDevice);

    AMidiOutputPort *outputPort;
    AMidiOutputPort_open(sNativeReceiveDevice, portNumber, &outputPort);

    // sMidiOutputPort.store(outputPort);
    sMidiOutputPort = outputPort;

    // Start read thread
    // pthread_init(true);
    /*int pthread_result =*/pthread_create(&sReadThread, nullptr, readThreadRoutine,
                                           nullptr);
}

/**
 * Native implementation of the (Java) TBMidiManager.stopReadingMidi() method.
 * @param   (unnamed)   JNI Env pointer.
 * @param   (unnamed)   TBMidiManager (Java) object.
 */
void Java_ly_pp_justpiano3_utils_MidiUtil_stopReadingMidi(JNIEnv *,
                                                          jclass clazz) {
    // need some synchronization here
    isReading = false;
    pthread_join(sReadThread, nullptr);

    /*media_status_t status =*/AMidiDevice_release(sNativeReceiveDevice);
    sNativeReceiveDevice = nullptr;
}

/**
 * Initializes JNI interface stuff, specifically the info needed to call back
 * into the Java layer when MIDI data is received.
 */
JNICALL void Java_ly_pp_justpiano3_utils_MidiUtil_registerCallBack(
        JNIEnv *env, jclass cls) {
    env->GetJavaVM(&theJvm);

    // Setup the receive data callback (into Java)
    jclass clsMainActivity =
            env->FindClass("ly/pp/justpiano3/utils/MidiUtil");
    dataCallbackClass = static_cast<jclass>(env->NewGlobalRef(cls));
    midDataCallback =
            env->GetStaticMethodID(clsMainActivity, "onNativeMessageReceive", "([B)V");
}

}  // extern "C"
