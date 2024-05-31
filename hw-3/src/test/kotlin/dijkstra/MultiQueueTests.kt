package dijkstra

import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicBoolean

class MultiQueueTest {
    @Test
    fun simpleTest() {
        val threads = 2
        val queues = 2
        val iterations = 20
        val queue = MultiQueue<Int>(queues)
        val jobs = mutableListOf<Job>()
        val data = Array(threads * iterations) { iterations * threads - it }
        val ans = Array(threads * iterations) { AtomicBoolean(false) }
        runBlocking {
            repeat(threads) { curTh ->
                jobs.add(launch(newSingleThreadContext(curTh.toString())) {
                    repeat(iterations) {
                        queue.add(data[it * threads + curTh])
                    }
                })
            }
            jobs.joinAll()
            jobs.clear()
            repeat(threads) { curTh ->
                jobs.add(launch(newSingleThreadContext(curTh.toString())) {
                    while (true) {
                        val result = queue.poll() ?: break
                        assertTrue { ans[result - 1].compareAndSet(false, true) }
                    }
                })
            }
            jobs.joinAll()
            for (i in ans)
                assertTrue(i.get())
        }
    }
}