package staks

import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class EBS<T>(private val collisionArraySize: Int = 6) : Stack<T>() {
    override suspend fun push(item: T) {
        stackOp(ThreadInfo(Operation.PUSH, Node(item)))
    }

    override suspend fun pop(): T? {
        val info = ThreadInfo<T>(Operation.POP, null)
        stackOp(info)
        return info.node?.item
    }

    enum class Operation { PUSH, POP }

    private class ThreadInfo<T>(val op: Operation, var node: Node<T>?) {
        val id = Thread.currentThread().id.toInt()
    }

    private val location = ConcurrentHashMap<Int, AtomicReference<ThreadInfo<T>>?>()
    private val collision = Array<AtomicReference<Int>>(collisionArraySize) { AtomicReference(null) }

    private fun stackOp(thread: ThreadInfo<T>) {
        if (!tryPerformStackOp(thread)) {
            lesOp(thread)
        }
    }

    private fun tryPerformStackOp(p: ThreadInfo<T>): Boolean {
        val phead: Node<T>?
        val pnext: Node<T>?
        when (p.op) {
            Operation.PUSH -> {
                phead = this.top.get()
                p.node?.next = phead
                return this.top.compareAndSet(phead, p.node)
            }

            Operation.POP -> {
                phead = this.top.get()
                if (phead == null) {
                    p.node = null
                    return true
                }
                pnext = phead.next
                if (this.top.compareAndSet(phead, pnext)) {
                    p.node = phead
                    return true
                } else {
                    p.node = null
                    return false
                }
            }
        }
    }

    private fun lesOp(p: ThreadInfo<T>) {
        while (true) {
            location[p.id] = AtomicReference(p)
            val pos = java.util.Random().nextInt(collision.size)
            var him = collision[pos].get()
            while (!collision[pos].compareAndSet(him, p.id)) {
                him = collision[pos].get()
            }
            if (him != null) {
                val q = location[him]?.get()
                if (q != null && q.id == him && q.op != p.op) {
                    if (location[p.id]?.compareAndSet(p, null) == true) {
                        if (tryCollison(p, q, him))
                            return
                        if (tryPerformStackOp(p))
                            return
                        continue
                    } else {
                        finishCollision(p)
                        return
                    }
                }
            }
            if (location[p.id]?.compareAndSet(p, null) == false) {
                finishCollision(p)
                return
            }
            if (tryPerformStackOp(p))
                return
        }
    }

    private fun finishCollision(p: ThreadInfo<T>) {
        if (p.op == Operation.POP) {
            p.node = location[p.id]?.get()?.node
            location[p.id]?.set(null)
        }
    }

    private fun tryCollison(p: ThreadInfo<T>, q: ThreadInfo<T>, him: Int): Boolean {
        return when (p.op) {
            Operation.PUSH -> location[him]?.compareAndSet(q, p) == true
            Operation.POP -> if (location[him]?.compareAndSet(q, null) == true) {
                p.node = q.node
                location[p.id]?.set(null)
                true
            } else false
        }
    }
}