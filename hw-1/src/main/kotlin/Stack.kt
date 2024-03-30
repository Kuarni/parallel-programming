import java.util.concurrent.atomic.AtomicReference

abstract class Stack<T> {
    @Volatile
    protected var top = AtomicReference<Node<T>>()
    abstract fun push(item: T)
    abstract fun pop(): T?

    fun head(): T? = top.get()?.item

    protected class Node<T>(val item: T) {
        var next: Node<T>? = null
    }
}