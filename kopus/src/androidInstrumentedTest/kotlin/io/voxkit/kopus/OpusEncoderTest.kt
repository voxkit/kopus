package io.voxkit.kopus

import org.junit.Test
import kotlin.math.sin
import kotlin.test.assertTrue

class OpusEncoderTest {
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
    fun testEncodeShorts() {
        val sampleRate = SampleRate.RATE_48K
        val pcm = generateSineWaveShorts(sampleRate)
        val buffer = ByteArray(OpusEncoder.DEFAULT_OUTPUT_BUFFER_SIZE)

        Opus.encoder(SampleRate.RATE_48K, Channel.STEREO, OpusApplication.AUDIO).use { encoder ->
            val len = encoder.encode(pcm, 960, buffer)
            assertTrue(len > 0, "Encoding failed, length should be greater than 0")
        }
    }

    @Test
    fun testEncodeFloats() {
        val sampleRate = SampleRate.RATE_48K
        val pcm = generateSineWaveFloats(sampleRate, channels = 2)
        val buffer = ByteArray(OpusEncoder.DEFAULT_OUTPUT_BUFFER_SIZE)

        Opus.encoder(SampleRate.RATE_48K, Channel.STEREO, OpusApplication.AUDIO).use { encoder ->
            val len = encoder.encode(pcm, 960, buffer)
            assertTrue(len > 0, "Encoding failed, length should be greater than 0")

        }
    }

    private fun generateSineWaveShorts(
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

    private fun generateSineWaveFloats(
        sampleRate: SampleRate,
        frequency: Int = 440,
        channels: Int = 1,
        durationMillis: Long = 1000L,
    ): FloatArray {
        val numSamples = (sampleRate.value / 1000 * durationMillis).toInt()
        return sinWave(sampleRate, frequency, channels)
            .take(numSamples * channels)
            .toList()
            .toFloatArray()
    }

    private fun sinWave(
        rate: SampleRate,
        frequency: Int = 440,
        channels: Int = 1,
        amplitude: Float = Float.MAX_VALUE,
    ): Sequence<Float> = sequence {
        var t = 0.0
        while (true) {
            val sample = amplitude * sin(2 * Math.PI * frequency * t / rate.value)
            repeat(channels) { yield(sample.toFloat()) }
            t += 1.0 / rate.value
        }
    }
}