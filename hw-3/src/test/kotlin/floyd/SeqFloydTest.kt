package floyd

import WeightedGraph
import dist
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SeqFloydTest {
    @Test
    fun simpleTest() {
        val graph: WeightedGraph = hashMapOf(
            "a" to arrayOf("b" dist 3, "c" dist 10),
            "b" to arrayOf("a" dist 3, "d" dist 100, "c" dist 2),
            "c" to arrayOf("a" dist 10, "b" dist 2, "d" dist 5, "e" dist 1),
            "e" to arrayOf("c" dist 1, "d" dist 1),
            "d" to arrayOf("b" dist 100, "c" dist 5, "e" dist 1),
        )

        assertEquals(7, floydSeq(graph, "a", "d"))
    }
}