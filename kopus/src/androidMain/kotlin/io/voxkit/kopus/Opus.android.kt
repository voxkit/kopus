package io.voxkit.kopus

public actual object Opus {
    public actual val version: String get() = nativeVersion()

    init {
        System.loadLibrary("kopus")
    }
    private external fun nativeVersion(): String
}
