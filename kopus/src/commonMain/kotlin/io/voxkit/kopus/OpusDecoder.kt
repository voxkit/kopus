package io.voxkit.kopus

/**
 * OpusDecoder provides methods to decode Opus data to PCM audio format.
 */
public interface OpusDecoder : AutoCloseable {
    /**
     * Decodes an Opus packet to 16-bit PCM samples.
     *
     * @param data Input Opus encoded data. Use `null` to indicate a packet loss (PLC).
     * @param frameSize Number of samples per channel of available space in pcm.
     * If this is less than the maximum packet duration (120ms; 5760 for 48kHz), this function
     * will not be capable of decoding some packets. In the case of PLC (data=null) or
     * FEC (decodeFec=true), then frameSize needs to be exactly the duration of audio that is missing,
     * otherwise the decoder will not be in the optimal state to decode the next incoming packet.
     * For the PLC and FEC cases, `frameSize` must be a multiple of 2.5 ms.
     * @param pcm Output buffer for decoded PCM data (interleaved if 2 channels).
     * @return Number of samples per channel decoded.
     */
    public fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: ShortArray,
        decodeFec: Boolean = false,
    ): Int

    /**
     * Decodes an Opus packet to floating-point PCM samples.
     *
     * @param data Input Opus encoded data. Use `null` to indicate a packet loss (PLC).
     * @param frameSize Number of samples per channel of available space in pcm.
     * If this is less than the maximum packet duration (120ms; 5760 for 48kHz), this function
     * will not be capable of decoding some packets. In the case of PLC (data=null) or
     * FEC (decodeFec=true), then frameSize needs to be exactly the duration of audio that is missing,
     * otherwise the decoder will not be in the optimal state to decode the next incoming packet.
     * For the PLC and FEC cases, `frameSize` must be a multiple of 2.5 ms.
     * @param pcm Output buffer for decoded PCM data (interleaved if 2 channels).
     * @return Number of samples per channel decoded.
     */
    public fun decode(
        data: ByteArray?,
        frameSize: Int,
        pcm: FloatArray,
        decodeFec: Boolean = false,
    ): Int
}

/**
 * Creates a new [OpusDecoder] instance.
 *
 * @param sampleRate Sample rate of output audio.
 * @param channel Number of audio channels: mono or stereo.
 * @return An [OpusDecoder] instance.
 */
public expect fun Opus.decoder(sampleRate: SampleRate, channel: Channel): OpusDecoder