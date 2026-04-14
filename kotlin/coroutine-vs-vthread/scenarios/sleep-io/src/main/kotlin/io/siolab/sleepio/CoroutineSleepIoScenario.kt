package io.siolab.sleepio

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.system.measureTimeMillis

class CoroutineSleepIoScenario : SleepIoScenario {
    override fun run(request: SleepIoRequest): SleepIoResult {
        val activeCoroutines = AtomicInteger(0)
        val maxConcurrentCoroutines = AtomicInteger(0)

        val elapsedMillis = measureTimeMillis {
            runBlocking {
                repeat(request.taskCount) {
                    launch {
                        val currentActiveCount = activeCoroutines.incrementAndGet()
                        maxConcurrentCoroutines.updateAndGet { previous ->
                            max(previous, currentActiveCount)
                        }

                        try {
                            delay(timeMillis = request.sleepMillis)
                        } finally {
                            activeCoroutines.decrementAndGet()
                        }
                    }
                }
            }
        }

        return SleepIoResult(
            request.taskCount,
            request.sleepMillis,
            maxConcurrentCoroutines.get(),
            elapsedMillis,
        )
    }
}
