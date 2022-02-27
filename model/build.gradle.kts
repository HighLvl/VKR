import com.google.protobuf.gradle.*
import io.netifi.flatbuffers.plugin.tasks.FlatBuffers
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.6.0"
    application
    id("com.google.protobuf") version "0.8.17"
    kotlin("plugin.serialization") version "1.6.0"
    id("io.netifi.flatbuffers") version "1.0.7"
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
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.8.18")
    implementation("com.google.protobuf:protobuf-kotlin:3.19.1")
// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation(kotlin("reflect"))
// https://mvnrepository.com/artifact/com.google.flatbuffers/flatbuffers-java
    implementation("com.google.flatbuffers:flatbuffers-java:2.0.3")
    implementation("com.charleskorn.kaml:kaml:0.38.0") // Get the latest version number from https://github.com/charleskorn/kaml/releases/latest
    implementation("io.grpc:grpc-kotlin-stub:1.2.0")
// https://mvnrepository.com/artifact/io.grpc/grpc-protobuf
    implementation("io.grpc:grpc-protobuf:1.43.0")
// https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-java
    implementation("io.grpc:protoc-gen-grpc-java:1.43.0")
    // https://mvnrepository.com/artifact/io.grpc/protoc-gen-grpc-kotlin
    implementation("io.grpc:protoc-gen-grpc-kotlin:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC3")
    implementation("io.grpc:grpc-stub:1.43.0")
    implementation("com.google.flatbuffers:flatbuffers-java-grpc:2.0.3")
    implementation("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.6.0")

    implementation("io.github.spair:imgui-java-app:$imguiVersion")
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")
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



flatbuffers {
    flatcPath = "C:\\flatbuffers\\flatc.exe"
    language = "java"
}
tasks {
    register<FlatBuffers>("createFlatBuffersKotlin") {
        outputDir = file("src/generated/flatbuffers")
        language = "java"
        extraArgs = "--grpc"
    }

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.43.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.2.0:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}