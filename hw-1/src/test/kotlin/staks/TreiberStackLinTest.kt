package staks

import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.*
import staks.TreiberStack

class TreiberStackLinTest {
    private val treiberStack = TreiberStack<Int>() // Initial state

    @Operation
    fun get() = treiberStack.head()

    @Operation
    suspend fun push(num: Int) = treiberStack.push(num)

    @Operation
    suspend fun pop() = treiberStack.pop()


    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun modelChecking() = ModelCheckingOptions().check(this::class)
}