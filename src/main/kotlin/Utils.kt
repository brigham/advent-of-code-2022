import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.Comparator

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/main/kotlin", "$name.txt")
    .also { it.createNewFile() }
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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