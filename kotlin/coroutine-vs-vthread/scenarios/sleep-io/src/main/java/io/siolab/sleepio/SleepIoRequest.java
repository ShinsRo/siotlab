package io.siolab.sleepio;

public record SleepIoRequest(
    int taskCount,
    long sleepMillis
) {
}
