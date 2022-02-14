plugins {
    kotlin("jvm") version "1.6.0"
}

group = "me.cherepanov"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.github.spair:imgui-java-app:1.86.2")

}