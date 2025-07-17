package io.voxkit.kopus

import org.junit.Test
import kotlin.test.assertEquals

class OpusEncoderTest {
    @Test
    fun testVersion() {
        val encoder = OpusEncoder()

        assertEquals("libopus 1.5.2", encoder.version)
    }
}
