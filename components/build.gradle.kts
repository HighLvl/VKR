import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    application
}

group = "me.cherepanov"
version = "1.0"

repositories {
    mavenCentral()
}

val imguiVersion = "1.86.2"

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("io.github.spair:imgui-java-app:$imguiVersion")
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")
    implementation(project(":core"))
    implementation("org.opt4j:opt4j-core:3.1.4")
    implementation("org.opt4j:opt4j-optimizers:3.1.4")
    // https://mvnrepository.com/artifact/org.opt4j/opt4j-viewer
    implementation("org.opt4j:opt4j-viewer:3.1.4")

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}