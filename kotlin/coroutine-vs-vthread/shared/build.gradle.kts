plugins {
    kotlin("jvm")
    `java-library`
}

group = "io.siolab"
version = "0.1.0"

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
