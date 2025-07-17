package io.voxkit.kopus

public actual class OpusEncoder {
    public actual val version: String get() = nativeVersion()

    private external fun nativeVersion(): String

    internal companion object {
        init {
            System.loadLibrary("kopus")
        }
    }
}
