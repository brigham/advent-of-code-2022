import kotlin.math.abs
import kotlin.math.sign

data class Movement(val dir: Char, val count: Int)

data class Position(val x: Int, val y: Int)

fun main() {
    fun parse(input: List<String>): List<Movement> {
        return input.map { it.split(' ') }
            .map { it[0] to it[1].toInt() }
            .map { Movement(it.first[0], it.second) }
    }

    fun expand(m: List<Movement>): List<Char> {
        return m.flatMap { m1 -> List(m1.count) { m1.dir } }
    }

    fun part1(input: List<String>): Int {
        val parsed = parse(input)
        val moves = expand(parsed)
        var head = Position(0, 0)
        var tail = Position(0, 0)
        val seen = mutableSetOf<Position>()
        for (move in moves) {
            head = when (move) {
                'U' -> head.copy(y = head.y - 1)
                'D' -> head.copy(y = head.y + 1)
                'L' -> head.copy(x = head.x - 1)
                'R' -> head.copy(x = head.x + 1)
                else -> error("unknown dir")
            }
            if (abs(head.x - tail.x) > 1 || abs(head.y - tail.y) > 1) {
                if (head.x - tail.x >= 2) {
                    tail = tail.copy(x = tail.x + 1, y = (if (head.y == tail.y) tail.y else (head.y - tail.y).sign + tail.y))
                } else if (head.x - tail.x <= -2) {
                    tail = tail.copy(x = tail.x - 1, y = (if (head.y == tail.y) tail.y else (head.y - tail.y).sign + tail.y))
                }
                if (head.y - tail.y >= 2) {
                    tail = tail.copy(y = tail.y + 1, x = (if (head.x == tail.x) tail.x else (head.x - tail.x).sign + tail.x))
                } else if (head.y - tail.y <= -2) {
                    tail = tail.copy(y = tail.y - 1, x = (if (head.x == tail.x) tail.x else (head.x - tail.x).sign + tail.x))
                }
            }
            seen.add(tail)
        }
        return seen.size
    }

    fun part2(input: List<String>): Int {
        val parsed = parse(input)
        val moves = expand(parsed)
        val rope = MutableList(10) { Position(0, 0) }
        val seen = mutableSetOf<Position>()
        for (move in moves) {
            for (headIndex in rope.indices.drop(0).dropLast(1)) {
                val tailIndex = headIndex + 1
                var head = rope[headIndex]
                var tail = rope[tailIndex]
                if (headIndex == 0) {
                    head = when (move) {
                        'U' -> head.copy(y = head.y - 1)
                        'D' -> head.copy(y = head.y + 1)
                        'L' -> head.copy(x = head.x - 1)
                        'R' -> head.copy(x = head.x + 1)
                        else -> error("unknown dir")
                    }
                }
                if (abs(head.x - tail.x) > 1 || abs(head.y - tail.y) > 1) {
                    if (head.x - tail.x >= 2) {
                        tail = tail.copy(
                            x = tail.x + 1,
                            y = (if (head.y == tail.y) tail.y else (head.y - tail.y).sign + tail.y)
                        )
                    } else if (head.x - tail.x <= -2) {
                        tail = tail.copy(
                            x = tail.x - 1,
                            y = (if (head.y == tail.y) tail.y else (head.y - tail.y).sign + tail.y)
                        )
                    }
                    if (head.y - tail.y >= 2) {
                        tail = tail.copy(
                            y = tail.y + 1,
                            x = (if (head.x == tail.x) tail.x else (head.x - tail.x).sign + tail.x)
                        )
                    } else if (head.y - tail.y <= -2) {
                        tail = tail.copy(
                            y = tail.y - 1,
                            x = (if (head.x == tail.x) tail.x else (head.x - tail.x).sign + tail.x)
                        )
                    }
                }
                rope[headIndex] = head
                rope[tailIndex] = tail
            }
            seen.add(rope.last())
        }
        return seen.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test2")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
