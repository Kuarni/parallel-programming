import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

fun generateGraph(verticesNum: Int, seed: Long = 0xebac0c, maxWeight: Int = 100, maxEdges: Int = 100): WeightedGraph {
    val random = Random(seed)
    val needToCreate = Array<Queue<Pair<Int, Int>>>(verticesNum) { LinkedList() }
    val graph: WeightedGraph = HashMap(Array(verticesNum) { curNode ->
        curNode to Array(
            (needToCreate[curNode].size) + (if (curNode + 1 != verticesNum) random.nextInt(maxEdges) else 0)
        ) {
            if (needToCreate[curNode].size != 0) {
                val (node, weight) = needToCreate[curNode].poll() ?: throw IllegalStateException("Null must be here.")
                node dist weight
            } else {
                val node = random.nextInt(curNode + 1, verticesNum)
                val weight = random.nextInt(1, maxWeight + 1)
                needToCreate[curNode].add(Pair(curNode, weight))
                node dist weight
            }
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
        mapper.readValue<HashMap<Int, Array<Edge>>>(
            jsonFile
        )
    return readGraph
}
