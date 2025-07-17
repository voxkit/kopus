package io.voxkit.kopus

import kotlin.test.Test
import kotlin.test.assertEquals

class OpusEncoderTest {
    @Test
    fun testVersion() {
        val encoder = OpusEncoder()

        assertEquals("1.5.1", encoder.version)
    }
}
