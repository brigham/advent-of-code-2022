import kotlinx.serialization.json.*

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
    fun parse(tree: JsonElement): Packet {
        return when (tree) {
            is JsonArray -> ListPacket(tree.asSequence().map { parse(it) }.toList())
            is JsonPrimitive -> ValPacket(tree.int)
            else -> error("only support nested arrays of number")
        }
    }

    fun parse(input: List<String>): List<Pair<Packet, Packet>> {
        val results = mutableListOf<Pair<Packet, Packet>>()
        for ((one, two, blank) in input.chunked(3)) {
            val treeOne = Json.parseToJsonElement(one)
            val treeTwo = Json.parseToJsonElement(two)
            results.add(parse(treeOne) to parse(treeTwo))
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
        val dp1 = parse(Json.parseToJsonElement("[[2]]"))
        val dp2 = parse(Json.parseToJsonElement("[[6]]"))
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
