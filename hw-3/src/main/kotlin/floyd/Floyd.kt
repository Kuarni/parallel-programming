package floyd

import WeightedGraph
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.math.min

fun floyd(graph: WeightedGraph, start: Int, destination: Int, ijPart: (Array<Array<Long>>, Int) -> Unit): Int {
    val dist = Array(graph.size) { Array(graph.size) { Int.MAX_VALUE.toLong() } }
    for ((node, edges) in graph) {
        dist[node][node] = 0
        for ((node2, weight) in edges) {
            dist[node][node2] = weight.toLong()
        }
    }
    for (k in dist.indices) {
        ijPart(dist, k)
    }

    return dist[start][destination].toInt()
}

fun floydSeq(graph: WeightedGraph, start: Int, destination: Int): Int =
    floyd(graph, start, destination) { dist: Array<Array<Long>>, k: Int ->
        for (i in dist.indices) {
            for (j in dist.indices) {
                if (dist[i][j] > dist[i][k] + dist[k][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }

fun floydParallel(graph: WeightedGraph, start: Int, destination: Int, numThreads: Int = 4): Int =
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