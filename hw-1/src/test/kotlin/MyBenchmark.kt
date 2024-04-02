package test

import staks.*
import kotlinx.benchmark.Benchmark
import org.openjdk.jmh.annotations.*
import kotlinx.benchmark.Scope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 0, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(iterations = 10)
class MyBenchmark {
    @Param("52", "69", "1488")
    var seed = 52

    val iterations = 1000

    enum class Operation {
        POP,
        PUSH
    }

    @Param("1", "2", "3", "4", "5", "6", "12", "24", "32", "64")
    var threads: Int = 6

    enum class Stack {
        Treiber,
        EBS
    }

    @Param("Treiber", "EBS")
    var stackEnum = Stack.Treiber

    @Benchmark
    fun stack() {
        val stack = when (stackEnum) {
            Stack.Treiber -> TreiberStack<Int>()
            Stack.EBS -> EBS()
        }
        val rand = Random(seed)

        runBlocking {
            repeat(threads) {
                launch(newSingleThreadContext(it.toString())) {
                    repeat(iterations) {
                        when (Operation.entries.random(rand)) {
                            Operation.POP -> stack.pop()
                            Operation.PUSH -> stack.push(rand.nextInt())
                        }
                    }
                }
            }
        }
    }
}