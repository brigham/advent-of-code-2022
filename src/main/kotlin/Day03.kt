fun main() {
    fun priority(only: Char) = when (only) {
        in 'A'..'Z' -> (only - 'A') + 27
        in 'a'..'z' -> (only - 'a') + 1
        else -> error("weird")
    }

    fun bisect(s: String): Pair<String, String> {
        val sz = s.length / 2
        return s.slice(0 until sz) to s.substring(sz)
    }

    fun part1(input: List<String>): Int {
        return input.map { bisect(it) }.map { it.first.toSet() to it.second.toSet() }
            .map { it.first.intersect(it.second).single() }.sumOf { priority(it) }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).map { chunk -> chunk.map { it.toSet() }.reduce { a, b -> a.intersect(b) } }
            .map { it.single() }.sumOf { priority(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
