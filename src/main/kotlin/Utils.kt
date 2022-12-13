import java.io.File
import java.util.*
import kotlin.collections.ArrayDeque

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/main/kotlin", "$name.txt")
    .also { it.createNewFile() }
    .readLines()

fun slurp(name: String): String = File("src/main/kotlin", "$name.txt")
    .also { it.createNewFile() }
    .readText()

fun <E> Sequence<E>.chunkedBy(predicate: (E) -> Boolean): Sequence<List<E>> {
    val outer = this
    var justYielded = false
    return sequence {
        val list = mutableListOf<E>()
        for (item in outer) {
            justYielded = false
            if (predicate(item)) {
                list.add(item)
            } else {
                yield(list.toList())
                list.clear()
                justYielded = true
            }
        }
        if (!justYielded) {
            yield(list.toList())
        }
    }
}

fun <E : Comparable<E>> heap(): PriorityQueue<E> {
    return PriorityQueue<E>()
}

fun <E : Comparable<E>> descendingHeap(): PriorityQueue<E> {
    return heap(naturalOrder<E>().reversed())
}

fun <E> heap(comparator: Comparator<E>): PriorityQueue<E> {
    return PriorityQueue<E>(comparator)
}

fun <E : Comparable<E>> min(a: E, b: E): E {
    return if (a < b) a else b
}

fun <E : Comparable<E>> max(a: E, b: E): E {
    return if (a > b) a else b
}

fun IntRange.overlap(other: IntRange): IntRange? {
    if (endInclusive < other.first || other.last < start) {
        return null
    }
    return max(start, other.first)..min(endInclusive, other.last)
}

fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> =
    transform(first) to transform(second)

// Graphs
data class GraphNode<N>(val steps: Int, val value: N)

fun <N> bfs(vararg start: N, next: (N) -> Sequence<N>, found: (N) -> Boolean): GraphNode<N>? {
    val q = ArrayDeque<GraphNode<N>>()
    val seen = mutableSetOf<N>()
    q.addAll(start.map { GraphNode(0, it) })
    while (q.isNotEmpty()) {
        val nextNode = q.removeFirst()
        if (nextNode.value in seen) {
            continue
        }
        seen.add(nextNode.value)
        if (found(nextNode.value)) {
            return nextNode
        }
        for (it in next(nextNode.value)) {
            q.add(GraphNode(nextNode.steps + 1, it))
        }
    }
    return null
}