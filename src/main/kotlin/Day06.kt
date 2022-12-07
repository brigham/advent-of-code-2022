fun main() {
    fun part1(input: List<String>): Int {
        val line = input[0]
        return line
            .windowedSequence(4, 1)
            .mapIndexed { index, s -> if (s.toSet().size == 4) index + 4 else null }
            .filterNotNull().first()
    }

    fun part2(input: List<String>): Int {
        val line = input[0]
        return line
            .windowedSequence(14, 1)
            .mapIndexed { index, s -> if (s.toSet().size == 14) index + 14 else null }
            .filterNotNull().first()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5)
    // check(part2(testInput) == 1)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
