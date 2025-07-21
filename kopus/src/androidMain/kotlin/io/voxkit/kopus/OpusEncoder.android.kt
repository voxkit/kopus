package io.voxkit.kopus

public actual fun Opus.encoder(
    sampleRate: SampleRate,
    channel: Channel,
    application: OpusApplication
): OpusEncoder = OpusEncoderImpl(
    sampleRate = sampleRate,
    channel = channel,
    application = application
)

private class OpusEncoderImpl(
    sampleRate: SampleRate,
    channel: Channel,
    application: OpusApplication
) : OpusEncoder {
    private var nativeEncoder: Long

    init {
        nativeEncoder = nativeInit(
            sampleRate = sampleRate.value,
            channels = channel.value,
            application = application.value,
        )
        check(nativeEncoder > 0) { "Failed to initialize Opus encoder." }
    }

    override fun encode(
        pcm: ShortArray,
        frameSize: Int,
        output: ByteArray
    ): Int {
        check(nativeEncoder > 0) { "Encoder has been closed." }
        return nativeEncode(nativeEncoder, pcm, frameSize, output)
    }

    override fun encode(
        pcm: FloatArray,
        frameSize: Int,
        output: ByteArray
    ): Int {
        check(nativeEncoder > 0) { "Encoder has been closed." }
        return nativeEncodeFloat(nativeEncoder, pcm, frameSize, output)
    }

    override fun close() {
        check(nativeEncoder > 0) { "Encoder has already been closed." }
        nativeClose(nativeEncoder)
        nativeEncoder = 0L
    }

    private external fun nativeInit(
        sampleRate: Int,
        channels: Int,
        application: Int,
    ): Long

    private external fun nativeEncode(
        nativeEncoder: Long,
        pcm: ShortArray,
        frameSize: Int,
        output: ByteArray
    ): Int

    private external fun nativeEncodeFloat(
        nativeEncoder: Long,
        pcm: FloatArray,
        frameSize: Int,
        output: ByteArray
    ): Int

    private external fun nativeClose(nativeEncoder: Long)
}