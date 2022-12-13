sealed class Packet: Comparable<Packet>

data class ListPacket(val contents: List<Packet>): Packet() {
    override fun compareTo(other: Packet): Int {
        return when (other) {
            is ListPacket -> {
                for (pair in this.contents.zip(other.contents)) {
                    val cmp = pair.first.compareTo(pair.second)
                    if (cmp != 0) {
                        return cmp
                    }
                }
                return this.contents.size.compareTo(other.contents.size)
            }
            is ValPacket -> this.compareTo(ListPacket(listOf(ValPacket(other.number))))
        }
    }

    override fun toString(): String {
        return "[" + contents.joinToString(",") + "]"
    }
}

data class ValPacket(val number: Int): Packet() {
    override fun compareTo(other: Packet): Int {
        return when (other) {
            is ValPacket -> this.number.compareTo(other.number)
            is ListPacket -> ListPacket(listOf(ValPacket(this.number))).compareTo(other)
        }
    }

    override fun toString(): String {
        return "$number"
    }
}

fun main() {
    fun parse(input: String, offset: MutableList<Int>): Packet {
        val ch = input[offset[0]]
        when (ch) {
            '[' -> {
                offset[0]++
                val contents = mutableListOf<Packet>()
                while (input[offset[0]] != ']') {
                    contents.add(parse(input, offset))
                    if (input[offset[0]] == ']') {
                        continue
                    }
                    check(input[offset[0]] == ',')
                    offset[0]++
                }
                offset[0]++
                return ListPacket(contents)
            }
            in '0'..'9' -> {
                var pos = offset[0]
                while (input[pos].isDigit()) {
                    pos++
                }
                val num = ValPacket(input.slice(offset[0] until pos).toInt())
                offset[0] = pos
                return num
            }
            else -> error("Bad ch: $ch")
        }
    }

    fun parse(input: List<String>): List<Pair<Packet, Packet>> {
        val results = mutableListOf<Pair<Packet, Packet>>()
        for ((one, two, blank) in input.chunked(3)) {
            results.add(parse(one, mutableListOf(0)) to parse(two, mutableListOf(0)))
            check(blank.isBlank())
        }
        return results.toList()
    }

    fun part1(input: List<String>): Int {
        var result = 0
        val pairs = parse(input)
        for (i in pairs.withIndex()) {
            if (i.value.inRightOrder()) {
                result += (i.index + 1)
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val dp1 = parse("[[2]]", mutableListOf(0))
        val dp2 = parse("[[6]]", mutableListOf(0))
        val allPackets = parse(input).flatMap { it.toList() } + listOf(dp1, dp2)
        val sorted = allPackets.sorted()
        return (sorted.indexOf(dp1) + 1) * (sorted.indexOf(dp2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private fun Pair<Packet, Packet>.inRightOrder(): Boolean {
    return first <= second
}
