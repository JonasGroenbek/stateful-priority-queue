interface Queue<T> {
    fun enqueue(e: T)
    fun dequeue() : T
    fun peek() : T
}