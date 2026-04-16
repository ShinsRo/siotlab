package io.siolab.httpio

import io.siolab.httpio.support.HttpIoContainers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class HttpIoContainerStartupTest : HttpIoContainers {
    @Test
    fun `nginx 테스트 컨테이너 base url 을 만든다`() {
        assertTrue(nginxBaseUrl.startsWith("http://"))
        println("nginxBaseUrl=$nginxBaseUrl")
    }
}
