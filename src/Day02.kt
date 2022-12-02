fun main() {
    fun part1(input: List<String>): Int {
        var score = 0
        for (line in input) {
            val (opp, me) = line.split(' ')
            score += when (opp) {
                "A" -> when (me) { "X" -> 3 "Y" -> 6 "Z" -> 0 else -> -999}
                "B" -> when (me) { "X" -> 0 "Y" -> 3 "Z" -> 6 else -> -999}
                "C" -> when (me) { "X" -> 6 "Y" -> 0 "Z" -> 3 else -> -999}
                else -> -999
            } + when (me) { "X" -> 1 "Y" -> 2 "Z" -> 3 else -> -999}
        }
        return score
    }

    fun part2(input: List<String>): Int {
        var score = 0
        for (line in input) {
            val (opp, me) = line.split(' ')
            score += when (opp) {
                "A" -> when (me) { "X" -> 3 "Y" -> 1 "Z" -> 2 else -> -999}
                "B" -> when (me) { "X" -> 1 "Y" -> 2 "Z" -> 3 else -> -999}
                "C" -> when (me) { "X" -> 2 "Y" -> 3 "Z" -> 1 else -> -999}
                else -> -999
            } + when (me) { "X" -> 0 "Y" -> 3 "Z" -> 6 else -> -999}
        }
        return score
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
