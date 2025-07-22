package io.voxkit.kopus

public actual object Opus {
    public actual val version: String get() = nativeVersion()

    init {
        System.loadLibrary("kopus")
    }

    public actual fun getErrorString(errorCode: Int): String = nativeGetErrorString(errorCode)

    private external fun nativeVersion(): String
    private external fun nativeGetErrorString(errorCode: Int): String
}
