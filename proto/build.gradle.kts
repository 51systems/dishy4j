import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    id("com.google.protobuf")
    idea
}

val grpcVersion : String by rootProject.extra
val krotoPlusVersion : String by rootProject.extra
val coroutinesVersion : String by rootProject.extra

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:$coroutinesVersion"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    api(platform("io.grpc:grpc-bom:$grpcVersion"))
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
    api("com.github.marcoferrer.krotoplus:kroto-plus-coroutines:$krotoPlusVersion")

    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    val protobufVersion : String by rootProject.extra

    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("kroto") {
            artifact = "com.github.marcoferrer.krotoplus:protoc-gen-kroto-plus:$krotoPlusVersion:jvm8@jar"
        }
    }

    generateProtoTasks {
        val krotoConfig = file("krotoPlusConfig.asciipb")
        all().forEach { task ->
            // Adding the config file to the task inputs lets UP-TO-DATE checks
            // include changes to configuration
            task.inputs.files + krotoConfig


            task.builtins {
//                get("java").apply {
//                    option("--descriptor_set_in=src/main/proto/dish.protoset")
//                    option("spacex/api/device/device.proto")
//                }
            }

            task.plugins {
                id("grpc")
                id("kroto") {
                    outputSubDir = "java"
                    option("ConfigPath=$krotoConfig")
                }
            }
        }
    }
}

idea {
    module {
        // proto files and generated Java files are automatically added as
        // source dirs.
        // If you have additional sources, add them here:
        sourceDirs + file("src/main/proto")
    }
}
repositories {
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}