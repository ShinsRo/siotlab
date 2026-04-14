package io.siolab.sleepio;

public record SleepIoResult(
    int taskCount,
    long sleepMillis,
    int maxConcurrentTasks,
    long elapsedMillis
) {
}
