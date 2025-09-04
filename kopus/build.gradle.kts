import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

val versionProperties = Properties()
val versionPropertiesFile = rootDir.resolve("version.properties")
versionPropertiesFile.takeIf { it.exists() }?.inputStream()?.use { versionProperties.load(it) }

group = "io.voxkit"
version = versionProperties["version"] ?: "0.0.0"

logger.lifecycle("Selected version name: ${project.version}")

kotlin {
    explicitApi()
//    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64 { configureOpusInterop() }
    iosArm64 { configureOpusInterop() }
    iosSimulatorArm64 { configureOpusInterop() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "io.voxkit.kopus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            cmake {
                arguments.addAll(
                    listOf(
                        "-DOPUS_BUILD_SHARED_LIBRARY=ON",
                        "-DOPUS_BUILD_TESTING=OFF",
                        "-DOPUS_BUILD_PROPGRAMS=OFF",
                    )
                )
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    ndkVersion = "28.2.13676358"

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    dependencies {
        androidTestImplementation(libs.kotlin.test)
        androidTestImplementation(libs.android.test.runner)
    }
}

fun KotlinNativeTarget.configureOpusInterop() {
    val targetName = targetName
    compilations.getByName("main") {
        cinterops.create("opus") {
            definitionFile.set(project.layout.buildDirectory.file("opus/${targetName}/def/opus.def"))
            includeDirs(rootProject.file("opus/include"))
        }
    }
}

tasks
    .filter { it.name.startsWith("cinteropOpus") && System.getProperty("os.name") == "Mac OS X" }
    .forEach { cinteropTask ->
        val platformName = cinteropTask.name.removePrefix("cinteropOpus")
        val opusBuildDir = project.layout.buildDirectory.dir("opus/$platformName").get()
        val cmakeDir = opusBuildDir.dir("cmake")
        val defDir = opusBuildDir.dir("def")
        val libDir = opusBuildDir.dir("lib")

        val generateOpusDefTask = tasks.register("generateOpusDef$platformName") {
            group = "interop"
            description = "Generates OPUS interop definition file for $platformName target."
            outputs.file(defDir.file("opus.def"))

            doFirst { defDir.asFile.mkdirs() }

            doLast {
                val defFileContent = """
                    headers = opus.h
                    staticLibraries = libopus.a
                    libraryPaths = build/opus/$platformName/cmake
                """.trimIndent()
                val defFile = defDir.file("opus.def")
                defFile.asFile.writeText(defFileContent)
            }
        }

        val opusSourcesDir = rootProject.file("opus")

        val osxSysroot = providers.exec {
            commandLine("xcode-select", "-print-path")
        }.standardOutput.asText.get().trim()

        val cmakeBin = providers.exec {
            commandLine("which", "cmake")
        }.standardOutput.asText.get().trim()

        val configureOpusTask = tasks.register<Exec>("configureOpusLib$platformName") {
            group = "build"
            description = "Builds the OPUS library for $platformName target."

            inputs.dir(opusSourcesDir)
            outputs.dir(cmakeDir)

            val xcodePlatform = when (platformName) {
                "IosX64" -> "iPhoneSimulator"
                "IosArm64" -> "iPhoneOS"
                "IosSimulatorArm64" -> "iPhoneSimulator"
                else -> throw IllegalArgumentException("Unsupported platform: $platformName")
            }
            val cmakeOsxArch = when (platformName) {
                "IosX64" -> "x86_64"
                "IosArm64" -> "arm64"
                "IosSimulatorArm64" -> "arm64"
                else -> throw IllegalArgumentException("Unsupported platform: $platformName")
            }
            val cmakeSystemName = when (platformName) {
                "IosX64", "IosSimulatorArm64", "IosArm64" -> "iOS"
                else -> throw IllegalArgumentException("Unsupported platform: $platformName")
            }

            doFirst {
                cmakeDir.asFile.mkdirs()

                args(
                    "-S",
                    opusSourcesDir,
                    "-B",
                    cmakeDir,
                    "-G",
                    "Unix Makefiles",
                    "-DCMAKE_SYSTEM_NAME=$cmakeSystemName",
                    "-DCMAKE_OSX_ARCHITECTURES=$cmakeOsxArch",
                    "-DCMAKE_OSX_SYSROOT=${osxSysroot}/Platforms/$xcodePlatform.platform/Developer/SDKs/$xcodePlatform.sdk",
                    "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=$libDir",
                )

                logger.lifecycle("Configuring OPUS for $platformName with command: $cmakeBin ${args?.joinToString(" ")}")
            }

            commandLine(cmakeBin)
        }

        val buildOpusTask = tasks.register<Exec>("buildOpusLib$platformName") {
            group = "build"
            description = "Builds the OPUS library for $platformName target."
            inputs.dir(cmakeDir)
            outputs.file(cmakeDir.file("libopus.a"))
            dependsOn(configureOpusTask.get())

            commandLine(cmakeBin, "--build", cmakeDir)
        }

        cinteropTask.dependsOn(buildOpusTask.get(), generateOpusDefTask.get())
    }

tasks.register<Delete>("cleanCxx") {
    dependsOn("externalNativeBuildCleanDebug")
    delete(project.file(".cxx"))
}

tasks.named("clean") { dependsOn("cleanCxx") }

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), project.name, version.toString())

    pom {
        name = "Kotlin OPUS Codec"
        description = "A Kotlin Multiplatform OPUS audio codec."
        inceptionYear = "2025"
        url = "https://github.com/voxkit/kopus"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "shepeliev"
                name = "Oleksandr Shepeliev"
                email = "a.shepeliev@gmail.com"
            }
        }
        scm {
            url = "https://github.com/voxkit/kopus"
            connection = "scm:git:https://github.com/voxkit/kopus.git"
            developerConnection = "scm:git:https://github.com/voxkit/kopus.git"
        }
    }
}
