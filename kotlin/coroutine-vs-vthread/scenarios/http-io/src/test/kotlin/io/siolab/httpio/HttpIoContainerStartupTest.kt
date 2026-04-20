package io.siolab.httpio

import io.siolab.httpio.support.HttpIoContainers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockserver.model.Delay
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Testcontainers
class HttpIoContainerStartupTest : HttpIoContainers {
    @Test
    fun `mock server 테스트 컨테이너가 HTTP 응답을 반환한다`() {
        mockServerClient()
            .`when`(request().withMethod("GET").withPath("/health"))
            .respond(response().withStatusCode(200).withBody("ok"))

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$mockServerBaseUrl/health"))
            .GET()
            .build()

        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, response.statusCode())
        assertEquals("ok", response.body())
        println("mockServerBaseUrl=$mockServerBaseUrl")
    }

    @Test
    fun `가상 스레드 http io 시나리오가 결과를 계산한다`() {
        mockServerClient()
            .`when`(request().withMethod("GET").withPath("/delay/50"))
            .respond(
                response()
                    .withStatusCode(200)
                    .withBody("ok")
                    .withDelay(Delay.milliseconds(50)),
            )

        val scenario = VirtualThreadHttpIoScenario()
        val result = scenario.run(
            HttpIoRequest(20, 50, 5),
            mockServerBaseUrl,
        )

        assertEquals(20, result.requestCount())
        assertEquals(50, result.serverDelayMillis())
        assertEquals(5, result.concurrency())
        assertTrue(result.maxConcurrentRequests() <= 5)
        assertTrue(result.elapsedMillis() > 0)
        assertTrue(result.requestsPerSecond() > 0.0)
        assertTrue(result.averageLatencyMillis() > 0.0)
    }
}
