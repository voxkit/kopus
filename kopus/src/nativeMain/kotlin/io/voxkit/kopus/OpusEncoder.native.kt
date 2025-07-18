package io.voxkit.kopus

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import opus.opus_get_version_string

public actual class OpusEncoder {
    @OptIn(ExperimentalForeignApi::class)
    public actual val version: String = opus_get_version_string()?.toKString() ?: "0.0.0"
}
