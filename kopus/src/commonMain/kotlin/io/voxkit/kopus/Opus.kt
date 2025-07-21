package io.voxkit.kopus

/**
 * Opus codec utilities.
 */
public expect object Opus {
    /**
     * Returns the Opus library version.
     */
    public val version: String

    /**
     * Returns a human-readable error string for the given error code.
     *
     * @param errorCode The Opus error code.
     * @return The error description.
     */
    public fun getErrorString(errorCode: Int): String
}

/**
 * Represents the number of audio channels.
 */
public enum class Channel(internal val value: Int) { MONO(1), STEREO(2) }

/**
 * Represents supported Opus sample rates.
 */
public enum class SampleRate(internal val value: Int) {
    RATE_8K(8000),
    RATE_12K(12000),
    RATE_16K(16000),
    RATE_24K(24000),
    RATE_48K(48000);
}

/**
 * Represents Opus application modes.
 */
public enum class OpusApplication(internal val value: Int) {
    VOIP(2048),
    AUDIO(2049),
    LOW_DELAY(2051)
}
