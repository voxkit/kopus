#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_io_voxkit_kopus_OpusEncoder_nativeVersion(JNIEnv *env, jobject) {
    std::string version = "1.5.1";
    return env->NewStringUTF(version.c_str());
}
