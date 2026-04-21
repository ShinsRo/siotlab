plugins {
    kotlin("jvm")
}

group = "io.siolab"
version = "0.1.0"

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.5"))

    implementation(project(":shared"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mockserver")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.mock-server:mockserver-client-java:5.15.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}
