import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.*

class TreiberStackLinTest {
    private val treiberStack = TreiberStack<Int>() // Initial state

    @Operation
    fun get() = treiberStack.head()

    @Operation
    fun push(num: Int) = treiberStack.push(num)

    @Operation
    fun pop() = treiberStack.pop()

    @Test // JUnit
    fun stressTest() = StressOptions().check(this::class)
}