package io.voxkit.kopus

import org.junit.Test
import kotlin.math.sin
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpusTest {
    @Test
    fun testVersion() {
        assertEquals("libopus 1.5.2", Opus.version)
    }

    @Test
    fun testGetErrorString() {
        assertEquals("success", Opus.getErrorString(0))
    }

    @Test
    fun testCreateEncoder() {
        val encoder = Opus.encoder(
            SampleRate.RATE_48K,
            channel = Channel.STEREO,
            application = OpusApplication.AUDIO,
        )
        encoder.close()
    }

    @Test
    fun testCreateDecoder() {
        val decoder = Opus.decoder(SampleRate.RATE_48K, channel = Channel.STEREO)
        decoder.close()
    }

    @Test
    fun testEncodeAndDecodeShorts() {
        val sampleRate = SampleRate.RATE_48K
        val channels = Channel.STEREO
        val timeMillis = 20L
        val frameSize = sampleRate.value / 1000 * 20 // 20 ms frame size
        val pcmInput = generateSineWaveOfShorts(
            sampleRate = sampleRate,
            channels = channels.value,
            durationMillis = timeMillis
        )
        val encodedBuffer = ByteArray(OpusEncoder.DEFAULT_OUTPUT_BUFFER_SIZE)
        val decodedBuffer = ShortArray(pcmInput.size)

        // Encode
        Opus.encoder(sampleRate, channels, OpusApplication.AUDIO).use { encoder ->
            val encodedLength = encoder.encode(pcmInput, frameSize, encodedBuffer)
            assertTrue(encodedLength > 0, "Encoding failed, length should be greater than 0")

            // Decode
            Opus.decoder(sampleRate, channels).use { decoder ->
                val encodedData = encodedBuffer.copyOf(encodedLength)
                val numberOfDecodedSamples = decoder.decode(encodedData, frameSize, decodedBuffer)
                assertEquals(
                    frameSize,
                    numberOfDecodedSamples,
                    "Decoded samples count should equal frame size"
                )
            }
        }
    }

    @Test
    fun testEncodeAndDecodeFloats() {
        val sampleRate = SampleRate.RATE_48K
        val channels = Channel.STEREO
        val timeMillis = 20L
        val frameSize = sampleRate.value / 1000 * 20 // 20 ms frame size
        val pcmInput = generateSineWaveOfFloats(
            sampleRate = sampleRate,
            channels = channels.value,
            durationMillis = timeMillis
        )
        val encodedBuffer = ByteArray(OpusEncoder.DEFAULT_OUTPUT_BUFFER_SIZE)
        val decodedBuffer = FloatArray(frameSize * 2) // 2 channels

        // Encode
        Opus.encoder(sampleRate, Channel.STEREO, OpusApplication.AUDIO).use { encoder ->
            val encodedLength = encoder.encode(pcmInput, frameSize, encodedBuffer)
            assertTrue(encodedLength > 0, "Encoding failed, length should be greater than 0")

            // Decode
            Opus.decoder(sampleRate, Channel.STEREO).use { decoder ->
                val encodedData = encodedBuffer.copyOf(encodedLength)
                val numberOfDecodedSamples = decoder.decode(encodedData, frameSize, decodedBuffer)
                assertEquals(
                    frameSize,
                    numberOfDecodedSamples,
                    "Decoded samples count should equal frame size"
                )
            }
        }
    }

    private fun generateSineWaveOfShorts(
        sampleRate: SampleRate,
        frequency: Int = 440,
        channels: Int = 1,
        durationMillis: Long = 1000L,
    ): ShortArray {
        val numSamples = (sampleRate.value / 1000 * durationMillis).toInt()
        return sinWave(sampleRate, frequency, channels, amplitude = Short.MAX_VALUE.toFloat())
            .take(numSamples * channels)
            .map { it.toInt().toShort() }
            .toList()
            .toShortArray()
    }

    private fun generateSineWaveOfFloats(
        sampleRate: SampleRate,
        frequency: Int = 440,
        channels: Int = 1,
        durationMillis: Long = 1000L,
    ): FloatArray {
        val numSamples = (sampleRate.value / 1000 * durationMillis).toInt()
        return sinWave(sampleRate, frequency, channels, amplitude = Float.MAX_VALUE)
            .take(numSamples * channels)
            .toList()
            .toFloatArray()
    }

    private fun sinWave(
        rate: SampleRate,
        frequency: Int,
        channels: Int,
        amplitude: Float,
    ): Sequence<Float> = sequence {
        var t = 0.0
        while (true) {
            val sample = amplitude * sin(2 * Math.PI * frequency * t / rate.value)
            repeat(channels) { yield(sample.toFloat()) }
            t += 1.0 / rate.value
        }
    }
}
