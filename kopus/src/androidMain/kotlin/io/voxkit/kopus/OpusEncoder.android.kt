package io.voxkit.kopus

public actual fun Opus.encoder(
    sampleRate: SampleRate,
    channels: Channels,
    application: OpusApplication
): OpusEncoder = OpusEncoderImpl(
    sampleRate = sampleRate,
    channels = channels,
    application = application
)

private class OpusEncoderImpl(
    sampleRate: SampleRate,
    channels: Channels,
    application: OpusApplication
) : OpusEncoder {
    private var nativeEncoderPtr: Long

    init {
        nativeEncoderPtr = nativeInit(
            sampleRate = sampleRate.value,
            channels = channels.value,
            application = application.value,
        )
        check(nativeEncoderPtr != 0L) { "Failed to initialize Opus encoder." }
    }

    override fun encode(
        pcm: ShortArray,
        frameSize: Int,
        output: ByteArray
    ): Int {
        check(nativeEncoderPtr != 0L) { "Encoder has been closed." }
        return nativeEncode(nativeEncoderPtr, pcm, frameSize, output)
    }

    override fun encode(
        pcm: FloatArray,
        frameSize: Int,
        output: ByteArray
    ): Int {
        check(nativeEncoderPtr != 0L) { "Encoder has been closed." }
        return nativeEncodeFloat(nativeEncoderPtr, pcm, frameSize, output)
    }

    override fun close() {
        check(nativeEncoderPtr != 0L) { "Encoder has already been closed." }
        nativeClose(nativeEncoderPtr)
        nativeEncoderPtr = 0L
    }

    private external fun nativeInit(
        sampleRate: Int,
        channels: Int,
        application: Int,
    ): Long

    private external fun nativeEncode(
        nativeEncoderPtr: Long,
        pcm: ShortArray,
        frameSize: Int,
        output: ByteArray
    ): Int

    private external fun nativeEncodeFloat(
        nativeEncoderPtr: Long,
        pcm: FloatArray,
        frameSize: Int,
        output: ByteArray
    ): Int

    private external fun nativeClose(nativeEncoderPtr: Long)
}
