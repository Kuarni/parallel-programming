import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

fun generateGraph(verticesNum: Int, seed: Long = 0xebac0c, maxWeight: Int = 100): WeightedGraph {
    val random = Random(seed)
    val needToCreate = HashMap<Int, Queue<Pair<Int, Int>>>()
    val graph: WeightedGraph = HashMap(Array(verticesNum) { curNode ->
        curNode.toString() to Array(
            (needToCreate[curNode]?.size ?: 0) + (if (curNode + 1 != verticesNum) random.nextInt(verticesNum) else 0)
        ) {
            val (node, weight) = if ((needToCreate[curNode]?.size ?: 0) != 0) {
                needToCreate[curNode]?.poll() ?: throw IllegalStateException("Null must be here.")
            } else {
                val node = random.nextInt(curNode + 1, verticesNum)
                val weight = random.nextInt(1, maxWeight + 1)
                needToCreate.getOrPut(node) { LinkedList() }.add(Pair(curNode, weight))
                node to weight
            }
            node.toString() dist weight
        }
    }.toMap())
    return graph
}

fun saveGraph(graph: WeightedGraph, name: String) {
    val jsonFile = File(name + "data.json")
    jsonFile.createNewFile()

    val mapper = jacksonObjectMapper()
    jsonFile.appendText(
        mapper.writeValueAsString(
            graph
        )
    )
}

fun loadGraph(path: String): WeightedGraph {
    val jsonFile = File(path)

    val mapper = jacksonObjectMapper()

    val readGraph: WeightedGraph =
        mapper.readValue<HashMap<String, Array<Edge>>>(
            jsonFile
        )
    return readGraph
}
