package staks

import kotlinx.coroutines.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Timeout
import java.util.concurrent.atomic.AtomicInteger

class CorrectTest {
    private val threads = 10
    private val iterations = 10000

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    fun popAfterPushTest(stack: Stack<Int>, name: String) {
        val vals = Array(iterations * threads) { false }
        val jobs = mutableListOf<Job>()
        runBlocking {
            repeat(threads) { curTh ->
                jobs.add(launch(newSingleThreadContext(curTh.toString())) {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                    }
                })
            }
            jobs.joinAll()
            jobs.clear()
            repeat(threads) {
                jobs.add(launch(newSingleThreadContext((threads + it).toString())) {
                    repeat(iterations) {
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                        }
                    }
                })
            }
            jobs.joinAll()
            assertEquals(null, stack.pop())
            for (it in vals) {
                assert(it)
            }
        }
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    @Timeout(1)
    fun popAndPushOneThreadTest(stack: Stack<Int>, name: String) {
        val vals = Array(iterations * threads) { false }
        val succeedPop = AtomicInteger(0)
        val jobs = mutableListOf<Job>()
        runBlocking {
            repeat(threads) { curTh ->
                jobs.add(launch(newSingleThreadContext(curTh.toString())) {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                            succeedPop.getAndIncrement()
                        }
                    }
                })
            }
            jobs.joinAll()
            while (succeedPop.get() < iterations*threads) {
                stack.pop()?.let { pos ->
                    assert(!vals[pos])
                    vals[pos] = true
                    succeedPop.getAndIncrement()
                }
            }
            assertEquals(null, stack.pop())
            for (it in vals) {
                assert(it)
            }
        }
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    @Timeout(1)
    fun popAndPushTest(stack: Stack<Int>, name: String) {
        val succeedPop = AtomicInteger(0)
        val vals = Array(iterations * threads) { false }
        val jobs = mutableListOf<Job>()
        runBlocking {
            repeat(threads) { curTh ->
                jobs.add(launch(newSingleThreadContext(curTh.toString())) {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                    }
                })
                jobs.add(launch(newSingleThreadContext((curTh + threads).toString())) {
                    repeat(iterations) {
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                            succeedPop.getAndIncrement()
                        }
                    }
                })
            }
            jobs.joinAll()
            while (succeedPop.get() < iterations*threads) {
                stack.pop()?.let { pos ->
                    assert(!vals[pos])
                    vals[pos] = true
                    succeedPop.getAndIncrement()
                }
            }
            assertEquals(null, stack.pop())
            for (it in vals) {
                assert(it)
            }
        }
    }
}