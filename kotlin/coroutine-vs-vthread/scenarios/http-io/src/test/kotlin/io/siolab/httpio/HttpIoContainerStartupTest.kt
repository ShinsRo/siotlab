package io.siolab.httpio

import io.siolab.httpio.support.HttpIoContainers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
}
