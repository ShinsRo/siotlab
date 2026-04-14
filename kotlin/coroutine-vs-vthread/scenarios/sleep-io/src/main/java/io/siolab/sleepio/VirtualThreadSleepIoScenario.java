package io.siolab.sleepio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadSleepIoScenario implements SleepIoScenario {
    @Override
    public SleepIoResult run(SleepIoRequest request) throws Exception {
        AtomicInteger activeThreads = new AtomicInteger(0);
        AtomicInteger maxConcurrentThreads = new AtomicInteger(0);
        List<Thread> threads = new ArrayList<>(request.taskCount());
        long startedAt = System.nanoTime();

        for (int i = 0; i < request.taskCount(); i++) {
            Thread thread = Thread.ofVirtual().start(() -> {
                int currentActiveCount = activeThreads.incrementAndGet();
                maxConcurrentThreads.updateAndGet(previous -> Math.max(previous, currentActiveCount));

                try {
                    Thread.sleep(request.sleepMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } finally {
                    activeThreads.decrementAndGet();
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;

        return new SleepIoResult(
            request.taskCount(),
            request.sleepMillis(),
            maxConcurrentThreads.get(),
            elapsedMillis
        );
    }
}
