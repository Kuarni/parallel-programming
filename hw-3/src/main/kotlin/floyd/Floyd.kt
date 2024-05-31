package floyd

import WeightedGraph

fun floydSeq(graph: WeightedGraph, start: String, destination: String): Int {
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
        for (i in dist.indices) {
            for (j in dist.indices) {
                if (dist[i][j] > dist[i][k] + dist[k][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }

    return dist[startInt][destinationInt].toInt()
}