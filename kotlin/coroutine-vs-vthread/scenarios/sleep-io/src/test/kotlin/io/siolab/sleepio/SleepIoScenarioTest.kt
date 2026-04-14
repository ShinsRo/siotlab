package io.siolab.sleepio

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Suppress("NonAsciiCharacters")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepIoScenarioTest {
    private val rows = mutableListOf<Row>()

    @ParameterizedTest(name = "{0} - 작업 수={1}, 대기 시간={2}ms")
    @MethodSource("cases")
    fun `sleep io 결과를 출력한다`(
        implementation: String,
        taskCount: Int,
        sleepMillis: Long,
        scenario: SleepIoScenario,
    ) {
        rows += Row(implementation, scenario.run(SleepIoRequest(taskCount, sleepMillis)))
    }

    @AfterAll
    fun `결과 테이블을 출력한다`() {
        println(headerFormat.format("구현", "작업 수", "대기(ms)", "총 수행 시간(ms)", "최대 동시 실행 수"))
        rows.sortedWith(
            compareBy<Row> { it.result.taskCount() }
                .thenBy { it.result.sleepMillis() }
                .thenBy { it.implementation }
        ).forEach(::println)
    }

    companion object {
        private val taskCounts = listOf(1_000, 10_000, 100_000)
        private val sleepMillis = listOf(1L, 100L, 500L)

        private val combinations = taskCounts.flatMap { taskCount ->
            sleepMillis.map { sleepMillis -> taskCount to sleepMillis }
        }

        @JvmStatic
        fun cases(): List<Arguments> {
            val coroutineCases = combinations.map { (taskCount, sleepMillis) ->
                Arguments.of("coroutine", taskCount, sleepMillis, CoroutineSleepIoScenario())
            }

            val virtualThreadCases = combinations.map { (taskCount, sleepMillis) ->
                Arguments.of("virtualThread", taskCount, sleepMillis, VirtualThreadSleepIoScenario())
            }

            return coroutineCases + virtualThreadCases
        }
    }
}


private const val headerFormat = "%-16s %-8s %-10s %-18s %-18s"
private const val resultFormat = "%-8d %-10d %-18d %-18d"

private fun SleepIoResult.toRowString(): String =
    resultFormat.format(taskCount(), sleepMillis(), elapsedMillis(), maxConcurrentTasks())

private data class Row(
    val implementation: String,
    val result: SleepIoResult,
) {
    override fun toString(): String = "%-16s %s".format(implementation, result.toRowString())
}