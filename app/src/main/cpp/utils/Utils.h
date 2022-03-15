#include <jni.h>
#include "cstdint"
#include "cmath"
#include "sys/stat.h"
#include "cstring"
#include "string"
#include "sstream"
#include "ios"

using namespace std;

template<typename K>
void fillArrayWithZeros(K *data, int32_t length) {
    size_t bufferSize = length * sizeof(K);
    memset(data, 0, bufferSize);
}

inline bool strEndedWith(string const &fullString, string const &ending) {
    if (fullString.length() >= ending.length()) {
        return (0 ==
                fullString.compare(fullString.length() - ending.length(), ending.length(), ending));
    } else {
        return false;
    }
}

inline long getSizeOfFile(FILE *fp) {

    if (!fp) {
        return -1;
    }

    int prev = ftell(fp);
    fseek(fp, 0L, SEEK_END);
    long sz = ftell(fp);

    fseek(fp, prev, SEEK_SET); //go back to where we were
    return sz;
}

inline char *java_str_to_c_str(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}
