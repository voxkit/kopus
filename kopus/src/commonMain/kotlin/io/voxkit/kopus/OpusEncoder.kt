package io.voxkit.kopus

/**
 * OpusEncoder provides methods to encode PCM audio data to Opus format.
 */
public interface OpusEncoder : AutoCloseable {
    /**
     * Encodes 16-bit PCM samples to Opus.
     *
     * @param pcm Input PCM data as a [ShortArray] (interleaved if 2 channels.
     * @param frameSize Number of samples per channel in the frame. This must be an Opus frame size
     * for the encoder's sampling rate. For example, at 48 kHz the permitted values are
     * 120, 240, 480, 960, 1920, and 2880. Passing in a duration of less than
     * 10 ms (480 samples at 48 kHz) will prevent the encoder from using the LPC or hybrid modes.
     * @param output Output buffer for encoded Opus data.
     * @return Number of bytes written to [output].
     */
    public fun encode(pcm: ShortArray, frameSize: Int, output: ByteArray): Int

    /**
     * Encodes floating-point PCM samples to Opus.
     *
     * @param pcm Input PCM data as a [FloatArray] (interleaved if 2 channels).
     * @param frameSize Number of samples per channel in the frame. This must be an Opus frame size
     * for the encoder's sampling rate. For example, at 48 kHz the permitted values are
     * 120, 240, 480, 960, 1920, and 2880. Passing in a duration of less than
     * 10 ms (480 samples at 48 kHz) will prevent the encoder from using the LPC or hybrid modes.
     * @param output Output buffer for encoded Opus data.
     * @return Number of bytes written to [output].
     */
    public fun encode(pcm: FloatArray, frameSize: Int, output: ByteArray): Int

    public companion object {
        /**
         * Default buffer size for Opus encoded output. It's set to 4000 bytes, as recommended by
         * the Opus documentation: https://opus-codec.org/docs/opus_api-1.5/group__opus__encoder.html.
         */
        public const val DEFAULT_OUTPUT_BUFFER_SIZE: Int = 4000
    }
}

/**
 * Creates a new [OpusEncoder] instance.
 *
 * @param sampleRate Sample rate of input audio.
 * @param channels Number of audio channels: mono or stereo.
 * @param application Opus application mode.
 * @return An [OpusEncoder] instance.
 */
public expect fun Opus.encoder(
    sampleRate: SampleRate,
    channels: Channels,
    application: OpusApplication
): OpusEncoder