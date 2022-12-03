fun main() {
    fun priority(only: Char) = when (only) {
        in 'A'..'Z' -> (only - 'A') + 27
        in 'a'..'z' -> (only - 'a') + 1
        else -> -999999999
    }

    fun part1(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val sz = line.length
            val sz2: Int = sz / 2
            check(sz2 * 2 == sz)
            val c1 =line.substring(0 until sz2)
            val c2 = line.substring(sz2)
            check(c1.length == c2.length)
            val s1 = c1.toSet()
            val s2 = c2.toSet()
            val itr = s1.intersect(s2)
            check(itr.size == 1)
            val only = itr.first()
            result += priority(only)
        }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0
        for (lines in input.chunked(3)) {
            val allIntersections = lines[0].toSet().intersect(lines[1].toSet().intersect(lines[2].toSet()))

            check(allIntersections.size == 1)
            val only = allIntersections.first()
            result += priority(only)
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
