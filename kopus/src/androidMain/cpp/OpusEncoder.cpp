#include <jni.h>
#include <string>
#include "opus.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_io_voxkit_kopus_OpusEncoderImpl_nativeInit(
        JNIEnv *env,
        jobject self,
        jint sampleRate,
        jint channels,
        jint application
) {
    int error;
    OpusEncoder *enc = opus_encoder_create(sampleRate, channels, application, &error);

    if (error != OPUS_OK) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exceptionClass != nullptr) {
            std::string opusError = opus_strerror(error);
            std::string errorMessage = "Failed to create Opus encoder: " + opusError;
            env->ThrowNew(exceptionClass, errorMessage.c_str());
        }
        return 0;
    }

    return reinterpret_cast<jlong>(enc);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_voxkit_kopus_OpusEncoderImpl_nativeEncode(
        JNIEnv *env,
        jobject self,
        jlong encPtr,
        jshortArray pcm,
        jint frameSize,
        jbyteArray output
) {
    OpusEncoder *enc = reinterpret_cast<OpusEncoder *>(encPtr);
    if (enc == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalStateException");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Opus encoder is not initialized.");
        }
        return -1;
    }

    jsize outputBufferSize = env->GetArrayLength(output);
    jshort *pcmData = env->GetShortArrayElements(pcm, nullptr);
    jbyte *outputData = env->GetByteArrayElements(output, nullptr);
    if (pcmData == nullptr || outputData == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/OutOfMemoryError");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Failed to allocate memory for PCM or output data.");
        }
        return -1;
    }

    int result = opus_encode(
            enc,
            pcmData,
            frameSize,
            reinterpret_cast<unsigned char *>(outputData),
            outputBufferSize);

    env->ReleaseShortArrayElements(pcm, pcmData, 0);
    env->ReleaseByteArrayElements(output, outputData, 0);

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_voxkit_kopus_OpusEncoderImpl_nativeEncodeFloat(
        JNIEnv *env,
        jobject self,
        jlong encPtr,
        jfloatArray pcm,
        jint frameSize,
        jbyteArray output
) {
    OpusEncoder *enc = reinterpret_cast<OpusEncoder *>(encPtr);
    if (enc == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalStateException");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Opus encoder is not initialized.");
        }
        return -1;
    }

    jsize outputBufferSize = env->GetArrayLength(output);
    jfloat *pcmData = env->GetFloatArrayElements(pcm, nullptr);
    jbyte *outputData = env->GetByteArrayElements(output, nullptr);
    if (pcmData == nullptr || outputData == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/OutOfMemoryError");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Failed to allocate memory for PCM or output data.");
        }
        return -1;
    }

    int result = opus_encode_float(
            enc,
            pcmData,
            frameSize,
            reinterpret_cast<unsigned char *>(outputData),
            outputBufferSize);

    env->ReleaseFloatArrayElements(pcm, pcmData, 0);
    env->ReleaseByteArrayElements(output, outputData, 0);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_io_voxkit_kopus_OpusEncoderImpl_nativeClose(
        JNIEnv *env,
        jobject self,
        jlong encPtr
) {
    OpusEncoder *encoder = reinterpret_cast<OpusEncoder *>(encPtr);
    if (encoder != nullptr) {
        opus_encoder_destroy(encoder);
    }
}