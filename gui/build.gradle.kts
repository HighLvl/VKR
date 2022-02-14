plugins {
    kotlin("jvm") version "1.6.0"
}

group = "me.cherepanov"
version = "1.0"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.2.3"
val imguiVersion = "1.78-1.1"
val imguiNatives = "imgui-java-natives-windows"
val lwjglNatives = "natives-windows"

dependencies {
    implementation("io.github.spair:imgui-java-app:1.86.2")
    implementation("io.github.spair:imgui-java-binding:1.86.2")

    implementation(kotlin("stdlib"))

    implementation(project(":model"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")


}