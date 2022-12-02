import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.PriorityQueue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
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
