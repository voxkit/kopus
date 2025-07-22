#include <jni.h>
#include <string>
#include "opus.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_io_voxkit_kopus_OpusDecoderImpl_nativeInit(
        JNIEnv *env,
        jobject self,
        jint sampleRate,
        jint channels
) {
    int error;
    OpusDecoder *dec = opus_decoder_create(sampleRate, channels, &error);

    if (error != OPUS_OK) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalArgumentException");
        if (exceptionClass != nullptr) {
            std::string opusError = opus_strerror(error);
            std::string errorMessage = "Failed to create Opus decoder: " + opusError;
            env->ThrowNew(exceptionClass, errorMessage.c_str());
        }
        return 0;
    }

    return reinterpret_cast<jlong>(dec);
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_voxkit_kopus_OpusDecoderImpl_nativeDecode(
        JNIEnv *env,
        jobject self,
        jlong decPtr,
        jbyteArray data,
        jint frameSize,
        jshortArray output,
        jboolean decodeFec
) {
    OpusDecoder *dec = reinterpret_cast<OpusDecoder *>(decPtr);
    if (dec == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalStateException");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Opus decoder is not initialized.");
        }
        return -1;
    }

    jsize dataSize = 0;
    jbyte *opusData = nullptr;
    if (data != nullptr) {
        dataSize = env->GetArrayLength(data);
        opusData = env->GetByteArrayElements(data, nullptr);
    }

    jshort *outputData = env->GetShortArrayElements(output, nullptr);
    if (opusData == nullptr || outputData == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/OutOfMemoryError");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Failed to allocate memory for Opus data or output data.");
        }
        return -1;
    }

    int result = opus_decode(
            dec,
            reinterpret_cast<const unsigned char *>(opusData),
            dataSize,
            outputData,
            frameSize,
            decodeFec ? 1 : 0);

    env->ReleaseByteArrayElements(data, opusData, 0);
    env->ReleaseShortArrayElements(output, outputData, 0);

    return result;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_voxkit_kopus_OpusDecoderImpl_nativeDecodeFloat(
        JNIEnv *env,
        jobject self,
        jlong decPtr,
        jbyteArray data,
        jint frameSize,
        jfloatArray output,
        jboolean decodeFec
) {
    OpusDecoder *dec = reinterpret_cast<OpusDecoder *>(decPtr);
    if (dec == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/IllegalStateException");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Opus decoder is not initialized.");
        }
        return -1;
    }

    jsize dataSize = 0;
    jbyte *opusData = nullptr;
    if (data != nullptr) {
        dataSize = env->GetArrayLength(data);
        opusData = env->GetByteArrayElements(data, nullptr);
    }

    jfloat *outputData = env->GetFloatArrayElements(output, nullptr);
    if (opusData == nullptr || outputData == nullptr) {
        jclass exceptionClass = env->FindClass("java/lang/OutOfMemoryError");
        if (exceptionClass != nullptr) {
            env->ThrowNew(exceptionClass, "Failed to allocate memory for Opus data or output data.");
        }
        return -1;
    }

    int result = opus_decode_float(
            dec,
            reinterpret_cast<const unsigned char *>(opusData),
            dataSize,
            outputData,
            frameSize,
            decodeFec ? 1 : 0);

    env->ReleaseByteArrayElements(data, opusData, 0);
    env->ReleaseFloatArrayElements(output, outputData, 0);

    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_io_voxkit_kopus_OpusDecoderImpl_nativeClose(
        JNIEnv *env,
        jobject self,
        jlong decPtr
) {
    OpusDecoder *decoder = reinterpret_cast<OpusDecoder *>(decPtr);
    if (decoder != nullptr) {
        opus_decoder_destroy(decoder);
    }
}