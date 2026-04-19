package io.siolab.httpio;

public record HttpIoResult(
    int requestCount,
    long serverDelayMillis,
    int concurrency,
    int maxConcurrentRequests,
    long elapsedMillis,
    double requestsPerSecond,
    double averageLatencyMillis,
    long p95LatencyMillis,
    long p99LatencyMillis
) {
}
