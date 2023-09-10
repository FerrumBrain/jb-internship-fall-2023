import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val node = Node<E>(null)
    private val head: AtomicReference<Node<E>> = AtomicReference(node)
    private val tail: AtomicReference<Node<E>> = AtomicReference(node)

    override fun enqueue(element: E) {
        val node = Node(element)
        while (true) {
            val t = tail.get()
            if (t.next.compareAndSet(null, node)) {
                tail.compareAndSet(t, node)
                break
            }
            tail.compareAndSet(t, t.next.get())
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val h = head.get()
            val t = tail.get()
            val next = h.next.get()
            if (h == t) {
                if (next == null) {
                    return null
                }
                tail.compareAndSet(t, next)
            } else {
                if (head.compareAndSet(h, next)) {
                    val snapshot = next!!.element
                    next.element = null
                    return snapshot
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
