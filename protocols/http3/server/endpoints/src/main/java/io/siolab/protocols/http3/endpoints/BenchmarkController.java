package io.siolab.protocols.http3.endpoints;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class BenchmarkController {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    @GetMapping("/ping")
    public Mono<Map<String, Object>> ping() {
        Map<String, Object> status = Map.of(
            "status", "ok",
            "timestamp", Instant.now().toString()
        );

        return Mono.just(status);
    }

    @GetMapping("/payload")
    public Mono<Map<String, Object>> payload(
        @RequestParam(name = "bytes", defaultValue = "1024") int bytes
    ) {
        int boundedBytes = Math.clamp(bytes, 0, 1024 * 1024);
        byte[] data = new byte[boundedBytes];
        ThreadLocalRandom.current().nextBytes(data);

        Map<String, Object> payload = Map.of(
            "bytes", boundedBytes,
            "data", ENCODER.encodeToString(data)
        );

        return Mono.just(payload);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> stream(
        @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        int boundedCount = Math.clamp(count, 1, 10_000);

        return Flux.range(1, boundedCount)
            .map(BenchmarkController::streamEvent);
    }

    private static Map<String, Object> streamEvent(int index) {
        return Map.of(
            "index", index,
            "timestamp", Instant.now().toString()
        );
    }
}
