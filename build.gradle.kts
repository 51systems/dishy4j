plugins {
    kotlin("jvm") version "1.4.21" apply false
    id("org.jetbrains.compose") version "0.3.0-build135" apply false
    id("com.google.protobuf") version "0.8.14" apply false
}

group = "dishy4j"
version = "0.1.0"

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}
