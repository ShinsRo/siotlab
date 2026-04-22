package io.siolab.httpio

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil
import kotlin.math.max
import kotlin.system.measureNanoTime

class CoroutineHttpIoScenario(
    private val httpClient: HttpClient = HttpIoHttpClients.newHttpClient(),
) : HttpIoScenario {
    override fun run(request: HttpIoRequest, baseUrl: String): HttpIoResult {
        val activeRequests = AtomicInteger(0)
        val maxConcurrentRequests = AtomicInteger(0)
        val latencies = mutableListOf<Long>()

        val elapsedNanos = measureNanoTime {
            runBlocking {
                coroutineScope {
                    List(request.requestCount()) {
                        async {
                            val currentActiveRequests = activeRequests.incrementAndGet()
                            maxConcurrentRequests.updateAndGet { previous ->
                                max(previous, currentActiveRequests)
                            }

                            val requestElapsedNanos = measureNanoTime {
                                try {
                                    val httpRequest = HttpRequest.newBuilder()
                                        .uri(URI.create("$baseUrl/delay/${request.serverDelayMillis()}"))
                                        .GET()
                                        .build()

                                    val response = httpClient.sendAsync(
                                        httpRequest,
                                        HttpResponse.BodyHandlers.discarding(),
                                    ).await()

                                    require(response.statusCode() == 200) {
                                        "Unexpected status code: ${response.statusCode()}"
                                    }
                                } finally {
                                    activeRequests.decrementAndGet()
                                }
                            }

                            synchronized(latencies) {
                                latencies += requestElapsedNanos / 1_000_000
                            }
                        }
                    }.awaitAll()
                }
            }
        }

        val elapsedMillis = elapsedNanos / 1_000_000
        val sortedLatencies = latencies.sorted()
        val requestsPerSecond = request.requestCount() / (elapsedMillis / 1_000.0)
        val averageLatencyMillis = sortedLatencies.average().takeIf { !it.isNaN() } ?: 0.0

        return HttpIoResult(
            request.requestCount(),
            request.serverDelayMillis(),
            maxConcurrentRequests.get(),
            elapsedMillis,
            requestsPerSecond,
            averageLatencyMillis,
            percentile(sortedLatencies, 0.95),
            percentile(sortedLatencies, 0.99),
        )
    }

    private fun percentile(sortedLatencies: List<Long>, percentile: Double): Long {
        if (sortedLatencies.isEmpty()) {
            return 0L
        }

        val index = ceil(percentile * sortedLatencies.size).toInt() - 1
        return sortedLatencies[index.coerceIn(0, sortedLatencies.lastIndex)]
    }
}
