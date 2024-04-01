import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class EBSLinTest {
    private val EBS = EBS<Int>()

    @Operation
    fun get() = EBS.head()

    @Operation
    suspend fun push(num: Int): Unit = EBS.push(num)

    @Operation
    suspend fun pop(): Int? = EBS.pop()

    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun modelChecking() = ModelCheckingOptions().check(this::class)
}