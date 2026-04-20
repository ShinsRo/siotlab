package io.siolab.httpio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class VirtualThreadHttpIoScenario implements HttpIoScenario {
    private final HttpClient httpClient;

    public VirtualThreadHttpIoScenario() {
        this(HttpIoHttpClients.newHttpClient());
    }

    public VirtualThreadHttpIoScenario(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpIoResult run(HttpIoRequest request, String baseUrl) throws Exception {
        AtomicInteger activeRequests = new AtomicInteger(0);
        AtomicInteger maxConcurrentRequests = new AtomicInteger(0);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>(request.requestCount()));
        Semaphore concurrencyLimiter = new Semaphore(request.concurrency());

        long startedAt = System.nanoTime();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Void>> futures = new ArrayList<>(request.requestCount());

            for (int i = 0; i < request.requestCount(); i++) {
                futures.add(executor.submit(() -> {
                    concurrencyLimiter.acquire();
                    int currentActiveRequests = activeRequests.incrementAndGet();
                    maxConcurrentRequests.updateAndGet(previous -> Math.max(previous, currentActiveRequests));

                    long requestStartedAt = System.nanoTime();
                    try {
                        HttpRequest httpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/delay/" + request.serverDelayMillis()))
                            .GET()
                            .build();

                        HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
                        if (response.statusCode() != 200) {
                            throw new IllegalStateException("Unexpected status code: " + response.statusCode());
                        }
                    } finally {
                        latencies.add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestStartedAt));
                        activeRequests.decrementAndGet();
                        concurrencyLimiter.release();
                    }

                    return null;
                }));
            }

            waitForAll(futures);
        }

        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        List<Long> sortedLatencies = new ArrayList<>(latencies);
        Collections.sort(sortedLatencies);

        double requestsPerSecond = request.requestCount() / (elapsedMillis / 1_000.0);
        double averageLatencyMillis = sortedLatencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        return new HttpIoResult(
            request.requestCount(),
            request.serverDelayMillis(),
            request.concurrency(),
            maxConcurrentRequests.get(),
            elapsedMillis,
            requestsPerSecond,
            averageLatencyMillis,
            percentile(sortedLatencies, 0.95),
            percentile(sortedLatencies, 0.99)
        );
    }

    private static void waitForAll(List<Future<Void>> futures) throws InterruptedException, ExecutionException {
        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private static long percentile(List<Long> sortedLatencies, double percentile) {
        if (sortedLatencies.isEmpty()) {
            return 0L;
        }

        int index = (int) Math.ceil(percentile * sortedLatencies.size()) - 1;
        return sortedLatencies.get(Math.max(0, Math.min(index, sortedLatencies.size() - 1)));
    }
}
