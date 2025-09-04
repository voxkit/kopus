# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Kopus** is a Kotlin Multiplatform library providing OPUS audio codec functionality for Android and iOS platforms. It wraps the libopus C library to provide native audio encoding/decoding capabilities across platforms.

## Architecture

- **Multiplatform Structure**: Uses Kotlin Multiplatform with platform-specific implementations
  - `commonMain`: Shared interfaces and data classes (`Opus`, `OpusEncoder`, `OpusDecoder`, enums)
  - `androidMain`: Android-specific implementation using JNI/NDK with C++ wrappers
  - `nativeMain`: iOS implementation using Kotlin/Native cinterop with libopus
- **Native Integration**: 
  - Android: Uses CMake to build C++ JNI bridge (`src/androidMain/cpp/`)
  - iOS: Uses cinterop to bind directly to libopus C library
- **OPUS Library**: Includes libopus source in `/opus` directory, built as static library per platform

## Key Components

- `Opus`: Main factory object providing version info and factory methods
- `OpusEncoder`: Handles audio encoding with configurable bitrate, sample rate, channels
- `OpusDecoder`: Handles audio decoding
- Platform-specific implementations in `*.android.kt` and `*.native.kt` files

## Common Development Commands

### Building
```bash
# Build all targets
./gradlew build

# Build specific platform
./gradlew :kopus:assembleRelease          # Android
./gradlew :kopus:linkDebugTestIosX64      # iOS Simulator
./gradlew :kopus:linkDebugTestIosArm64    # iOS Device
```

### Testing  
```bash
# Run all tests
./gradlew test

# Platform-specific tests
./gradlew :kopus:testDebugUnitTest        # Android unit tests
./gradlew :kopus:connectedAndroidTest     # Android instrumented tests
./gradlew :kopus:iosX64Test               # iOS Simulator tests
./gradlew :kopus:iosArm64Test             # iOS Device tests (requires device)
```

### Cleanup
```bash
# Clean all build artifacts
./gradlew clean

# Clean native C++ build artifacts
./gradlew cleanCxx
```

## Development Notes

- **Platform Detection**: iOS-specific build tasks only run on macOS (`System.getProperty("os.name") == "Mac OS X"`)
- **Native Library Building**: Custom Gradle tasks automatically build libopus for iOS targets using CMake
- **CMake Integration**: Uses CMake 3.22.1+ for building native components
- **NDK Version**: Android builds use NDK 28.2.13676358
- **API Design**: Uses `expect`/`actual` pattern for multiplatform APIs with resource management (`Closeable`)

## File Structure

```
kopus/
├── src/
│   ├── commonMain/kotlin/io/voxkit/kopus/     # Shared interfaces
│   ├── androidMain/
│   │   ├── cpp/                               # JNI/NDK C++ code
│   │   └── kotlin/io/voxkit/kopus/           # Android implementations
│   ├── nativeMain/kotlin/io/voxkit/kopus/    # iOS implementations
│   ├── commonTest/kotlin/                    # Shared tests
│   └── androidInstrumentedTest/kotlin/       # Android instrumented tests
opus/                                         # libopus C source code
```

## Publishing

Configured for Maven Central publishing via Sonatype Central Portal with automatic signing. Version and coordinates defined in `kopus/build.gradle.kts`.