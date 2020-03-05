@Suppress("UNCHECKED_CAST")
class PriorityQueue <T : Comparable<T>>(data: Array<T>, private var size: Int, private val comparator: Comparator<T>?) : Queue<T> {

    enum class State {
        ASCENDING, DESCENDING, COMPARATOR
    }
    
    private var state : State = State.ASCENDING
    private var heap : Array<T?>

    init {
        if (size > data.size) error("Initial array is too small for initial size.")
        this.heap = data.sliceArray(0..size) as Array<T?>
        for(pos in ((size - 1)/ 2) downTo 0) heapify(pos)
    }
    override fun enqueue(e: T) {
        insert(e)
    }
    override fun dequeue(): T {
        return extract()
    }

    override fun peek(): T {
        return heap[size] as T
    }

    fun changeState(value: Int){
        when(value) {
            0 -> state = State.ASCENDING
            1 -> state = State.DESCENDING
            else -> state = State.COMPARATOR
        }
    }

    /**
     * performing a heapsort
     * @return a full Array<T> without null elements
     */
    fun sort() : Array<T> {
        for(partition in size - 1 downTo 0) {
            swap(0, partition)
            sink(0, partition -1)
        }
        val sortedHeap = heap.sliceArray(0 until size)
        //since heap sort is inplace and reverses the order, the heap structure is broken
        heapify(0)
        return sortedHeap as Array<T>
    }

    /**
     * NOTE - could have replaced the heapify calls with swim/sink had they not been recursive.
     * That is probably a better solution since heapify has to be wrapped in a loop.
     * @param pos the position of the root of which subtree to heapify
     */
    private fun heapify(pos: Int){
        //basecase
        if(shouldSustain(pos, size - 1)) return

        val lc = leftChild(pos)
        val rc = rightChild(pos)

        if(rc > size - 1){
            swap(lc, pos)
            return
        }
        if(lessThan(lc, rc)){
            swap(rc, pos)
            heapify(rc)
        } else {
            swap(lc, pos)
            heapify(lc)
        }
    }

    /**
     * This is used as a check whether a sink/heapify should be sustained
     * NOTE - could probably be made a lot cleaner.
     */
    private fun shouldSustain(pos: Int, partition: Int): Boolean {
        if(isLeaf(pos) || size == 0 || leftChild(pos) > partition) return true
        return if(rightChild(pos) > partition)   (lessThan(leftChild(pos), pos))
        else   (lessThan(leftChild(pos), pos) && (lessThan(rightChild(pos), pos)))
    }

    /**
     * Recursively swim an element up the heap to maintain a heapifified state
     */
    private fun swim(pos: Int){
        //basecase
        if(lessThan(parent(pos), pos)){
            swap(parent(pos), pos)
            swim(parent(pos))
        }
    }

    /**
     * Recursively sink an element down the tree, prioritizing swapping the larger child
     * @param pos the position of the element to sink
     * @param partition defines the position to scope the sink to. For full sink size - 1
     */
    private fun sink(pos: Int, partition: Int){
        //basecase
        if(shouldSustain(pos, partition)) return

        if(rightChild(pos) > partition){
            swap(pos, leftChild(pos))
        } else {
            if(lessThan(leftChild(pos), rightChild(pos))){
                swap(pos, rightChild(pos))
                sink(rightChild(pos), partition)
            } else {
                swap(pos, leftChild(pos))
                sink(leftChild(pos), partition)
            }

        }
    }

    //Magic
    private fun swap(pos1: Int, pos2: Int) {
        heap[pos1] = heap[pos2].also { heap[pos2] = heap[pos1] }
    }

    /**
     * NOTE - ArrayIndexOutOfBounds probably should not be handled here, but for now it makes sense
     * @param pos1 position of element that checks if it is less than the other return true
     * @param pos2 position of element that checks if it is greater than the other return true
     */
    private fun lessThan(pos1: Int, pos2: Int) : Boolean {
        return if (pos2 > size || heap[pos2] == null) false
        else {
            when(state){
                State.ASCENDING -> heap[pos1]?.compareTo(heap[pos2]!!)!! < 0
                State.DESCENDING -> heap[pos1]?.compareTo(heap[pos2]!!)!! > 0
                State.COMPARATOR -> comparator?.compare(heap[pos1], heap[pos2])!! > 0
            }
        }
    }

    private fun insert(e: T){
        increaseSize()
        heap[size] = e
        swim(size - 1)
        size++
    }

    private fun allocate(): Int = if (size == 0)  1 else size * 2

    /**
     * increases the amount of elements the array allocates if it is currently capped
     */
    private fun increaseSize(){
        if(size == heap.size || size == 0){
            heap = heap.copyOf(allocate())
        }
    }

    /**
     * Extracting and returning the top element from the heap, setting empty value to null
     */
    private fun extract() : T {
        val e = heap[0]
        swap(0, --size)
        heap[size] = null
        sink(0, size)
        return e as T
    }

    private fun rightChild(pos: Int) : Int{
        return (pos + 1) * 2
    }

    private fun leftChild(pos: Int) : Int{
        return (pos + 1) * 2 - 1
    }

    private fun parent(pos: Int) : Int {
        return (pos - 1) / 2
    }

    private fun isLeaf(pos: Int) : Boolean{
        return (size - 1) / 2 < pos
    }
}

fun main() {
    val arr = arrayOf(10,34,23,5,23,4567,34,23423,764)
    val pq = PriorityQueue(arr, 6, null)

    val sorted : Array<Int> = pq.sort()
    pq.changeState(1)
    val sorted2: Array<Int> = pq.sort()

    for(i in sorted.indices){
        println(sorted[i])
    }

    for(i in sorted2.indices){
        println(sorted2[i])
    }
}