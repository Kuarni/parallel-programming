package test

import staks.*
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.OutputTimeUnit
import org.openjdk.jmh.annotations.*
import kotlinx.benchmark.Scope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
class MyBenchmark {
    @Param("-1", "-2", "52", "69", "1488")
    var seed = 52

    val iterations = 1000000

    enum class Operation {
        POP,
        PUSH
    }

    @Param("1", "2", "3", "4", "5", "6", "8", "12", "24")
    var threads: Int = 6

    enum class Stack {
        Treiber,
        EBS
    }

    @Param("Treiber", "EBS")
    var stackEnum = Stack.Treiber

    var data: Array<Array<Pair<Operation, Int>>> = arrayOf()

    @Setup(Level.Trial)
    fun setup() {
        val rand = Random(seed)
        data = Array(threads)  { thread ->
            when (seed) {
                -1 -> Array(iterations) { Pair(Operation.entries[(thread + it) % 2], 1) }
                -2 -> Array(iterations) { Pair(Operation.entries[thread % 2], 1) }
                else -> Array(iterations) { Pair(Operation.entries.random(rand), rand.nextInt()) }
            }
        }
    }

    @Benchmark
    fun stack() {
        val stack = when (stackEnum) {
            Stack.Treiber -> TreiberStack<Int>()
            Stack.EBS -> EBS()
        }

        var counter = 0
        runBlocking {
            for (forThread in data) {
                launch(newSingleThreadContext((counter++).toString())) {
                    for (iterations in forThread) {
                        when (iterations.first) {
                            Operation.POP -> stack.pop()
                            Operation.PUSH -> stack.push(iterations.second)
                        }
                    }
                }
            }
        }
    }
}