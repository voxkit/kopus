package io.voxkit.kopus

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import opus.opus_get_version_string
import opus.opus_strerror

@OptIn(ExperimentalForeignApi::class)
public actual object Opus {
    public actual val version: String = opus_get_version_string()?.toKString() ?: "0.0.0"

    public actual fun getErrorString(errorCode: Int): String =
        opus_strerror(errorCode)?.toKString() ?: "Unknown error"
}
