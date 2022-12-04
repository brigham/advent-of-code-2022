fun toRange(l: List<String>): IntRange {
    return (l[0].toInt())..(l[1].toInt())
}

fun main() {
    fun contains(r1: IntRange, r2: IntRange) =
        r1.first <= r2.first && r1.last >= r2.last

    fun overlap(r1: IntRange, r2: IntRange) =
        !(r1.last < r2.first || r2.last < r1.first)

    fun part1(input: List<String>): Int {
        return input.map { it.split(',') }
            .map { toRange(it[0].split('-')) to toRange(it[1].split('-')) }
            .map { contains(it.first, it.second) || contains(it.second, it.first) }.count { it }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.split(',') }
            .map { toRange(it[0].split('-')) to toRange(it[1].split('-')) }
            .map { overlap(it.first, it.second) }.count { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
