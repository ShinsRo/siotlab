package io.siolab.httpio;

public record HttpIoRequest(
    int requestCount,
    long serverDelayMillis
) {
}
