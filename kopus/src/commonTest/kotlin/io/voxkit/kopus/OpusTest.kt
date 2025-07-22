package io.voxkit.kopus

import kotlin.test.Test
import kotlin.test.assertEquals

class OpusTest {
    @Test
    fun testVersion() {
        assertEquals("libopus 1.5.2", Opus.version)
    }
}
