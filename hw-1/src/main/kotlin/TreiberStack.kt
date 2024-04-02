class TreiberStack<T> : Stack<T>() {
    override suspend fun push(item: T) {
        val newHead = Node(item)
        var oldHead: Node<T>?

        do {
            oldHead = top.get()
            newHead.next = oldHead
        } while (!top.compareAndSet(oldHead, newHead))
    }

    override suspend fun pop(): T? {
        var oldHead: Node<T>
        var newHead: Node<T>?

        do {
            oldHead = top.get() ?: return null
            newHead = oldHead.next
        } while (!top.compareAndSet(oldHead, newHead))

        return oldHead.item
    }
}