package dijkstra

import WeightedGraph
import dist
import loadGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SeqDijkstraTest {
    @Test
    fun simpleTest() {
        val graph: WeightedGraph = hashMapOf(
            0 to arrayOf(1 dist 3, 2 dist 10),
            1 to arrayOf(0 dist 3, 3 dist 100, 2 dist 2),
            2 to arrayOf(0 dist 10, 1 dist 2, 3 dist 5, 4 dist 1),
            3 to arrayOf(2 dist 1, 2 dist 1),
            4 to arrayOf(3 dist 100, 2 dist 5, 4 dist 1),
        )

        assertEquals(10, dijkstraSeq(graph, 0, 3))
    }

    @Test
    fun load100Test() {
        val graph: WeightedGraph = loadGraph("graphs/graph100data.json")

        assertEquals(9, dijkstraSeq(graph, 0, 99))
    }

    @Test
    fun load1000Test() {
        val graph: WeightedGraph = loadGraph("graphs/graph1000data.json")

        assertEquals(3, dijkstraSeq(graph, 0, 999))
    }

    @Test
    fun load10000Test() {
        val graph: WeightedGraph = loadGraph("graphs/graph10000data.json")

        assertEquals(25, dijkstraSeq(graph, 0, 9999))
    }

//    @Test
//    fun load100000Test() {
//        val graph: WeightedGraph = loadGraph("graphs/graph100000data.json")
//
//        assertEquals(21, dijkstraSeq(graph, 0, 99999))
//    }
}