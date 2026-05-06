plugins {
    java
    id("org.springframework.boot") version "4.0.6" apply false
}

group = "io.siolab.protocols"
version = "0.0.1-SNAPSHOT"

val springBootVersion = "4.0.6"

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java")

    dependencies {
        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        add("testImplementation", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
        add("testImplementation", "io.projectreactor:reactor-test")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

configure(subprojects.filter { it.path.startsWith(":bootstraps:") }) {
    dependencies {
        add("implementation", project(":endpoints"))
        add("implementation", "org.springframework.boot:spring-boot-starter-actuator")
        add("implementation", "org.springframework.boot:spring-boot-starter-webflux")
    }
}
