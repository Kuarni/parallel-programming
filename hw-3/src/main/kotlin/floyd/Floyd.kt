package floyd

import WeightedGraph
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun floyd(graph: WeightedGraph, start: String, destination: String, ijPart: (Array<Array<Long>>, Int) -> Unit): Int {
    var counter = 0
    var startInt = 0
    var destinationInt = 0
    val nodesToInt = HashMap(graph.map { (k, _) ->
        if (k == start) startInt = counter
        if (k == destination) destinationInt = counter
        k to counter++
    }.toMap())
    val dist = Array(nodesToInt.size) { Array<Long>(nodesToInt.size) { Int.MAX_VALUE.toLong() } }
    for ((node, edges) in graph) {
        val num = nodesToInt[node] ?: throw IllegalStateException("Invalid node $node")
        dist[num][num] = 0
        for ((node2, weight) in edges) {
            dist[num][nodesToInt[node2] ?: throw IllegalStateException("Invalid node $node")] = weight.toLong()
        }
    }
    for (k in dist.indices) {
        ijPart(dist, k)
    }

    return dist[startInt][destinationInt].toInt()
}

fun floydSeq(graph: WeightedGraph, start: String, destination: String): Int =
    floyd(graph, start, destination) { dist: Array<Array<Long>>, k: Int ->
        for (i in dist.indices) {
            for (j in dist.indices) {
                if (dist[i][j] > dist[i][k] + dist[k][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }

fun floydParallel(graph: WeightedGraph, start: String, destination: String, numThreads: Int = 4): Int =
    floyd(graph, start, destination) { dist: Array<Array<Long>>, k: Int ->
        val segment = dist.size / numThreads +  if (dist.size % numThreads != 0) 1 else 0
        runBlocking {
            repeat(numThreads) {
                val segmentMin = it * segment
                val segmentMax = min((it + 1) * segment, dist.size)
                if (segmentMin < dist.size)
                    launch(newSingleThreadContext(it.toString())) {
                        for (i in segmentMin until segmentMax) {
                            for (j in dist.indices) {
                                if (dist[i][j] > dist[i][k] + dist[k][j]) {
                                    dist[i][j] = dist[i][k] + dist[k][j]
                                }
                            }
                        }
                    }
            }
        }
    }