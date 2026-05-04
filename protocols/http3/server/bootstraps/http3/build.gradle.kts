plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("io.netty.incubator:netty-incubator-codec-http3:0.0.28.Final")
    implementation("io.netty.incubator:netty-incubator-codec-native-quic:0.0.71.Final")
}
