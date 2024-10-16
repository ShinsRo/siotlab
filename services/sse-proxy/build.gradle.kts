import org.jetbrains.kotlin.com.intellij.openapi.util.SystemInfo
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

repositories {
    mavenCentral()
}

dependencies {
    if (SystemInfo.isMac && "aarch64" in SystemInfo.OS_ARCH) {
        runtimeOnly(variantOf(libs.netty.resolver.dns.macos) { classifier("osx-aarch_64") })
    }

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.logging)

    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.redis.reactive)
    implementation(libs.spring.cloud.starter.gateway)

    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")

    implementation(platform(libs.otel.instrumentation.bom))
    implementation(libs.otel.instrumentation.spring.boot)
}
