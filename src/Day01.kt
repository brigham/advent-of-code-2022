fun main() {
    fun solve(input: List<String>, n: Int = 1): List<Int> {
        val calHeap = descendingHeap<Int>()
        input.asSequence().chunkedBy { it.isNotBlank() }
            .map { it.map { it.toInt() }.sum() }
            .forEach { calHeap.add(it) }

        return (0 until n).map { calHeap.remove() }.toList()
    }

    fun part1(input: List<String>): Int {
        return solve(input)[0]
    }

    fun part2(input: List<String>): Int {
        return solve(input, 3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 25000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
