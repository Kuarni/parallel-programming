plugins {
    id("org.jetbrains.kotlinx.benchmark") version "0.4.10"
    kotlin("plugin.allopen") version "1.9.20"
    kotlin("jvm") version "1.9.23"
    kotlin("kapt") version "1.9.23"
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
    testImplementation("org.jetbrains.kotlinx:lincheck:2.28.1")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

benchmark {
    targets {
        register("test")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    jvmArgs(
        "--add-opens",
        "java.base/java.lang=ALL-UNNAMED",
        "--add-opens",
        "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-exports",
        "java.base/jdk.internal.util=ALL-UNNAMED",
        "--add-exports",
        "java.base/sun.security.action=ALL-UNNAMED"
    )
}

kotlin {
    jvmToolchain(17)
}