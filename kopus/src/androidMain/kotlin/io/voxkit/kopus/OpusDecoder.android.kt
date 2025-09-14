package io.voxkit.kopus

public actual fun Opus.decoder(sampleRate: SampleRate, channels: Channels): OpusDecoder =
    OpusDecoderImpl(sampleRate, channels)

private class OpusDecoderImpl(sampleRate: SampleRate, channels: Channels) : OpusDecoder {
    private var nativeDecoder: Long

    init {
        nativeDecoder = nativeInit(
            sampleRate = sampleRate.value,
            channels = channels.value,
        )
        check(nativeDecoder != 0L) { "Failed to initialize Opus decoder." }
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean,
    ): Int {
        check(nativeDecoder != 0L) { "Decoder has been closed." }
        return nativeDecode(nativeDecoder, data, frameSize, pcm, decodeFec)
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean,
    ): Int {
        check(nativeDecoder != 0L) { "Decoder has been closed." }
        return nativeDecodeFloat(nativeDecoder, data, frameSize, pcm, decodeFec)
    }

    override fun close() {
        check(nativeDecoder != 0L) { "Decoder has already been closed." }
        nativeClose(nativeDecoder)
        nativeDecoder = 0L
    }

    private external fun nativeInit(sampleRate: Int, channels: Int): Long

    private external fun nativeDecode(
        nativeDecoder: Long,
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean,
    ): Int

    private external fun nativeDecodeFloat(
        nativeDecoder: Long,
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean,
    ): Int

    private external fun nativeClose(nativeDecoder: Long)
}
