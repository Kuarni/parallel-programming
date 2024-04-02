import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Timeout
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class CorrectTest {
    private val seed = 52
    private val threads = 20
    private val iterations = 10000

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    fun popAfterPushTest(stack: Stack<Int>, name: String) {
        val vals = Array(iterations * threads) { false }
        runBlocking {
            repeat(threads) { curTh ->
                launch {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                    }
                }
            }
            delay(5)
            repeat(threads) {
                launch {
                    repeat(iterations) {
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                        }
                    }
                }
            }
            delay(5)
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
        runBlocking {
            repeat(threads) { curTh ->
                launch {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                            succeedPop.getAndIncrement()
                        }
                    }
                }
            }
            delay(5)
            while (succeedPop.get() < iterations) {
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
        runBlocking {
            repeat(threads) { curTh ->
                launch {
                    repeat(iterations) {
                        stack.push(it * threads + curTh)
                    }
                }
                launch {
                    repeat(iterations) {
                        stack.pop()?.let { pos ->
                            assert(!vals[pos])
                            vals[pos] = true
                            succeedPop.getAndIncrement()
                        }
                    }
                }
            }
            delay(5)
            while (succeedPop.get() < iterations) {
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