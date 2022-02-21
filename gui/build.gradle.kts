import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "me.cherepanov"
version = "1.0"

repositories {
    mavenCentral()
}

val imguiVersion = "1.86.2"
val imguiNatives = "imgui-java-natives-windows"
val lwjglNatives = when(OperatingSystem.current()) {
    OperatingSystem.LINUX -> "natives-linux"
    OperatingSystem.MAC_OS -> "natives-macos"
    OperatingSystem.WINDOWS -> "natives-windows"
    else -> ""
}
val lwjglVersion = "3.2.3"

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":model"))

    implementation("io.github.spair:imgui-java-app:$imguiVersion")
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    implementation("com.google.guava:guava:31.0.1-jre")

    implementation("org.lwjgl:lwjgl-nfd:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl-nfd:$lwjglVersion:$lwjglNatives")
    implementation(kotlin("script-runtime"))
}