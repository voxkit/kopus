package io.voxkit.kopus

public actual fun Opus.decoder(sampleRate: SampleRate, channels: Channels): OpusDecoder =
    OpusDecoderImpl(sampleRate, channels)

private class OpusDecoderImpl(sampleRate: SampleRate, channels: Channels) : OpusDecoder {
    private var nativeDecoderPtr: Long

    init {
        nativeDecoderPtr = nativeInit(
            sampleRate = sampleRate.value,
            channels = channels.value,
        )
        check(nativeDecoderPtr != 0L) { "Failed to initialize Opus decoder." }
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean,
    ): Int {
        check(nativeDecoderPtr != 0L) { "Decoder has been closed." }
        return nativeDecode(nativeDecoderPtr, data, frameSize, pcm, decodeFec)
    }

    override fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean,
    ): Int {
        check(nativeDecoderPtr != 0L) { "Decoder has been closed." }
        return nativeDecodeFloat(nativeDecoderPtr, data, frameSize, pcm, decodeFec)
    }

    override fun close() {
        check(nativeDecoderPtr != 0L) { "Decoder has already been closed." }
        nativeClose(nativeDecoderPtr)
        nativeDecoderPtr = 0L
    }

    private external fun nativeInit(sampleRate: Int, channels: Int): Long

    private external fun nativeDecode(
        nativeDecoderPtr: Long,
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean,
    ): Int

    private external fun nativeDecodeFloat(
        nativeDecoderPtr: Long,
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean,
    ): Int

    private external fun nativeClose(nativeDecoderPtr: Long)
}
