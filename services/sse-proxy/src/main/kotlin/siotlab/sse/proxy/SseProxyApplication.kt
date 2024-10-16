package siotlab.sse.proxy

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SseProxyApplication

fun main() {
    SpringApplication.run(SseProxyApplication::class.java)
}
