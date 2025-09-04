# Kopus

A Kotlin Multiplatform library providing OPUS audio codec functionality for Android and iOS platforms.

[![Maven Central](https://img.shields.io/maven-central/v/io.voxkit/kopus)](https://search.maven.org/artifact/io.voxkit/kopus)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Features

- **Cross-platform**: Supports Android and iOS through Kotlin Multiplatform
- **High-quality audio**: Based on the libopus 1.5.2 codec
- **Flexible encoding**: Support for multiple sample rates, channels, and application modes
- **Multiple data formats**: Encode/decode both 16-bit PCM (Short) and floating-point (Float) audio

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.voxkit:kopus:1.0.0")
}
```

## Quick Start

### Basic Encoding and Decoding

```kotlin
import io.voxkit.kopus.*

// Create encoder
val encoder = Opus.encoder(
    sampleRate = SampleRate.RATE_48K,
    channels = Channels.STEREO,
    application = OpusApplication.AUDIO
)

// Create decoder  
val decoder = Opus.decoder(
    sampleRate = SampleRate.RATE_48K,
    channels = Channels.STEREO
)

// Encode PCM data
val pcmInput: ShortArray = // your audio samples
val frameSize = 960 // 20ms at 48kHz
val encodedBuffer = ByteArray(OpusEncoder.DEFAULT_OUTPUT_BUFFER_SIZE)
val encodedLength = encoder.encode(pcmInput, frameSize, encodedBuffer)

// Decode back to PCM
val decodedBuffer = ShortArray(frameSize * 2) // 2 channels
val encodedData = encodedBuffer.copyOf(encodedLength)
val decodedSamples = decoder.decode(encodedData, frameSize, decodedBuffer)

// Don't forget to close resources
encoder.close()
decoder.close()
```

## Configuration Options

### Sample Rates
- `SampleRate.RATE_8K` (8 kHz)
- `SampleRate.RATE_12K` (12 kHz) 
- `SampleRate.RATE_16K` (16 kHz)
- `SampleRate.RATE_24K` (24 kHz)
- `SampleRate.RATE_48K` (48 kHz) 

### Channels
- `Channels.MONO` - Single channel
- `Channels.STEREO` - Two channels

### Application Modes
- `OpusApplication.VOIP` - Optimize for voice calls
- `OpusApplication.AUDIO` - Optimize for music/general audio
- `OpusApplication.LOW_DELAY` - Optimize for low-latency applications

## Advanced Features

### Packet Loss Concealment (PLC)

```kotlin
// Handle missing packets by passing null data
val missingSamples = decoder.decode(
    data = null, // indicates packet loss
    frameSize = expectedFrameSize,
    pcm = outputBuffer
)
```

### Forward Error Correction (FEC)

```kotlin
// Decode with FEC to recover from errors
val recoveredSamples = decoder.decode(
    data = corruptedPacket,
    frameSize = expectedFrameSize, 
    pcm = outputBuffer,
    decodeFec = true
)
```

## Platform Support

| Platform | Status            | Implementation          |
|----------|-------------------|-------------------------|
| Android  | ✅ Supported       | JNI/NDK with C++ bridge |
| iOS      | ✅ Supported       | Kotlin/Native cinterop  |


## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Links

- [OPUS Codec](https://opus-codec.org/)
- [libopus Documentation](https://opus-codec.org/docs/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
