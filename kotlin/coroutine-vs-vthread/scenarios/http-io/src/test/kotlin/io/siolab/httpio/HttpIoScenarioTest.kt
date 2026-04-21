package io.siolab.httpio

import io.siolab.httpio.support.HttpIoContainers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockserver.model.Delay
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.testcontainers.junit.jupiter.Testcontainers

@Suppress("NonAsciiCharacters")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpIoScenarioTest : HttpIoContainers {
    private val rows = mutableListOf<Row>()

    @BeforeAll
    fun `mock server 응답을 준비한다`() {
        listOf(1L, 50L).forEach { delayMillis ->
            mockServerClient()
                .`when`(request().withMethod("GET").withPath("/delay/$delayMillis"))
                .respond(
                    response()
                        .withStatusCode(200)
                        .withBody("ok")
                        .withDelay(Delay.milliseconds(delayMillis)),
                )
        }
    }

    @ParameterizedTest(name = "{0} - 요청 수={1}, 지연={2}ms, 동시성={3}")
    @MethodSource("cases")
    fun `http io 결과를 수집한다`(
        implementation: String,
        requestCount: Int,
        serverDelayMillis: Long,
        concurrency: Int,
        scenario: HttpIoScenario,
    ) {
        val result = scenario.run(
            HttpIoRequest(requestCount, serverDelayMillis, concurrency),
            mockServerBaseUrl,
        )

        rows += Row(implementation, result)
    }

    @AfterAll
    fun `결과 테이블을 출력한다`() {
        println("구현\t요청 수\t지연(ms)\t동시성\t총 수행 시간(ms)\t최대 동시 요청 수\treq/s\t평균 지연\tp95\tp99")
        rows.sortedWith(
            compareBy<Row> { it.result.requestCount() }
                .thenBy { it.result.serverDelayMillis() }
                .thenBy { it.implementation }
        ).forEach(::println)
    }

    companion object {
        @JvmStatic
        fun cases(): List<Arguments> = listOf(
            Arguments.of("coroutine", 100, 1L, 10, CoroutineHttpIoScenario()),
            Arguments.of("coroutine", 100, 50L, 10, CoroutineHttpIoScenario()),
            Arguments.of("coroutine", 1_000, 1L, 100, CoroutineHttpIoScenario()),
            Arguments.of("coroutine", 1_000, 50L, 100, CoroutineHttpIoScenario()),
            Arguments.of("virtualThread", 100, 1L, 10, VirtualThreadHttpIoScenario()),
            Arguments.of("virtualThread", 100, 50L, 10, VirtualThreadHttpIoScenario()),
            Arguments.of("virtualThread", 1_000, 1L, 100, VirtualThreadHttpIoScenario()),
            Arguments.of("virtualThread", 1_000, 50L, 100, VirtualThreadHttpIoScenario()),
        )
    }

    private data class Row(
        val implementation: String,
        val result: HttpIoResult,
    ) {
        override fun toString(): String = "$implementation\t${result.toRowString()}"
    }
}

private fun HttpIoResult.toRowString(): String = listOf(
    requestCount(),
    serverDelayMillis(),
    concurrency(),
    elapsedMillis(),
    maxConcurrentRequests(),
    "%.2f".format(requestsPerSecond()),
    "%.2f".format(averageLatencyMillis()),
    p95LatencyMillis(),
    p99LatencyMillis(),
).joinToString("\t")
