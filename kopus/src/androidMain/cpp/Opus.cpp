#include <jni.h>
#include <string>
#include "opus.h"

extern "C" JNIEXPORT jstring JNICALL Java_io_voxkit_kopus_Opus_nativeVersion(JNIEnv *env, jobject) {
    const char *version = opus_get_version_string();
    return env->NewStringUTF(version);
}
