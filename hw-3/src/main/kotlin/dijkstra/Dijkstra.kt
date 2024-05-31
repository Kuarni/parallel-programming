package dijkstra

import Edge
import WeightedGraph
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun dijkstraSeq(graph: WeightedGraph, start: Int, destination: Int): Int {
    val nodeInfo = HashMap<Int, Int>().withDefault { Int.MAX_VALUE }
    val visited = mutableSetOf<Int>()
    val queue = PriorityQueue<Edge>()

    queue.add(Edge(start, 0))
    nodeInfo[start] = 0

    while (true) {
        val (node, curDistance) = queue.poll() ?: break
        if (visited.add(node)) {
            graph[node]?.forEach { (node, weight) ->
                val totalDist = weight + curDistance
                if (totalDist < nodeInfo.getValue(node)) {
                    nodeInfo[node] = totalDist
                    queue.add(Edge(node, totalDist))
                }
            } ?: throw IllegalStateException("Node $node not found in graph")
        }
    }

    return nodeInfo[destination] ?: throw IllegalStateException("No destination node found")
}


fun dijkstraParallel(
    graph: WeightedGraph,
    start: Int,
    destination: Int,
    queuesNum: Int = 4,
    threadNum: Int = 4,
    seed: Long = 0xEBAC0C,
    sleepTimeNanoseconds: Long = 100
): Int {
    val nodeInfo = HashMap(graph.mapValues { AtomicInteger(Int.MAX_VALUE) })
    val queue = MultiQueue<Edge>(queuesNum, seed)
    val threadProgression = Array(threadNum) { true }

    runBlocking {
        queue.add(Edge(start, 0))
        nodeInfo[start] = AtomicInteger(0)

        repeat(threadNum) {
            launch(newSingleThreadContext(it.toString())) {
                while (true) {
                    val service = queue.poll()
                    if (service == null) {
                        threadProgression[it] = false
                        if (!threadProgression.contains(true))
                            break
                        TimeUnit.NANOSECONDS.sleep(sleepTimeNanoseconds)
                        continue
                    }
                    val (node, curDistance) = service
                    graph[node]?.forEach { (node, weight) ->
                        val totalDist = weight + curDistance
                        while (true) {
                            val oldDistance = nodeInfo.getValue(node).get()
                            if (totalDist < oldDistance) {
                                if (nodeInfo.getValue(node).compareAndSet(oldDistance, totalDist))
                                    queue.add(Edge(node, totalDist))
                            } else break
                        }
                    } ?: throw IllegalStateException("Node $node not found in graph")
                }
            }
        }
    }

    val result = nodeInfo[destination]?.get()
    if (result == Int.MAX_VALUE || result == null) {
        throw IllegalStateException("No destination node found")
    }
    return result
}
