pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "http3-server"

include(
    "endpoints",
    "bootstraps:http1",
    "bootstraps:http2",
    "bootstraps:http3",
)
