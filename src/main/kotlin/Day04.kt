fun toRange(s: String): IntRange {
    val (start, end) = s.split(',')
    return (start.toInt())..(end.toInt())
}

fun toRange(l: List<String>): IntRange {
    return (l[0].toInt())..(l[1].toInt())
}

fun main() {
    fun parse(input: List<String>) = input.map { it.split(',') }
        .map { it[0] to it[1] }
        .map { pair -> pair.map { toRange(it) } }

    fun part1(input: List<String>): Int {
        return parse(input)
            .map { it.toList().contains(it.first.overlap(it.second)) }
            .count { it }
    }

    fun part2(input: List<String>): Int {
        return parse(input)
            .map { it.first.overlap(it.second) != null }
            .count { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
