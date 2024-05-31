package dijkstra

import kotlinx.coroutines.sync.Mutex
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class MultiQueue<T : Comparable<T>>(queuesNum: Int, seed: Long = 0xEBAC0C) {
    private val queues = Array(queuesNum) { Pair(Mutex(), PriorityQueue<T>()) }
    private val random = Random(seed)
    private val size = AtomicInteger(0)

    init {
        if (queuesNum < 1) {
            throw IllegalArgumentException("Queue num of MultiQueue should be greater or equal to 1.")
        }
    }

    fun add(element: T) {
        var q: Pair<Mutex, PriorityQueue<T>>
        do {
            q = queues.random(random)
        } while (!q.first.tryLock())
        q.second.add(element)
        q.first.unlock()
        size.incrementAndGet()
    }

    fun poll(): T? {
        while (true) {
            if (size.get() == 0)
                return null
            var q1: Pair<Mutex, PriorityQueue<T>>
            var q2: Pair<Mutex, PriorityQueue<T>>
            do {
                q1 = queues.random(random)
                q2 = queues.random(random)
                if (q1.second.peek()?.let { a -> q2.second.peek()?.let { b -> a > b } == true } == true)
                    q1 = q2.also { q2 = q1 }
            } while (!q1.first.tryLock())
            val el = q1.second.poll()
            q1.first.unlock()
            if (el == null)
                continue
            size.decrementAndGet()
            return el
        }
    }
}