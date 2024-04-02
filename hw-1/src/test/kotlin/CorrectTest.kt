import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Timeout
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class CorrectTest {
    private val seed = 52
    private val maxThreads = 20
    private val maxIter = 10000

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    fun popAfterPushTest(stack: Stack<Int>, name: String) {
        val threads = Random(seed).nextInt(1, maxThreads + 1)
        val iterations = Random(seed).nextInt(1, maxIter + 1)
        runBlocking {
            repeat(iterations) {
                repeat(threads) {
                    launch {
                        stack.push(it)
                    }
                }
            }
            delay(5)
            repeat(iterations) {
                repeat(threads) {
                    launch {
                        assertNotEquals(null, stack.pop())
                    }
                }
            }
            delay(5)
            assertEquals(null, stack.pop())
        }
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    @Timeout(1)
    fun popAndPushOneThreadTest(stack: Stack<Int>, name: String) {
        val threads = Random(seed).nextInt(1, maxThreads + 1)
        val iterations = Random(seed).nextInt(1, maxIter + 1)
        var succeedPop = AtomicInteger(0)
        runBlocking {
            repeat(iterations) {
                repeat(threads) {
                    launch {
                        stack.push(it)
                        if (stack.pop() != null) succeedPop.getAndIncrement()
                    }
                }
            }
            delay(5)
            while (succeedPop.get() < iterations) {
                if (stack.pop() != null) succeedPop.getAndIncrement()
            }
            assertEquals(null, stack.pop())
        }
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(StackArguments::class)
    @Timeout(1)
    fun popAndPushTest(stack: Stack<Int>, name: String) {
        val threads = Random(seed).nextInt(1, maxThreads + 1)
        val iterations = Random(seed).nextInt(1, maxIter + 1)
        var succeedPop = AtomicInteger(0)
        runBlocking {
            repeat(iterations) {
                repeat(threads) {
                    launch {
                        stack.push(it)
                    }
                    launch {
                        if (stack.pop() != null) succeedPop.getAndIncrement()
                    }
                }
            }
            delay(5)
            while (succeedPop.get() < iterations) {
                if (stack.pop() != null) succeedPop.getAndIncrement()
            }
            assertEquals(null, stack.pop())
        }
    }
}