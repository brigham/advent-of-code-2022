import java.util.ArrayDeque
import java.util.Deque
import java.util.regex.Pattern

data class Instruction(val times: Int, val from: Int, val to: Int)

fun main() {
    fun parse(input: List<String>): Pair<List<Deque<Char>>, List<Instruction>> {
        val stacks = mutableListOf<Deque<Char>>()

        fun ensureSize(n: Int) {
            if (stacks.size < n) {
                for (i in (stacks.size until n)) {
                    stacks.add(ArrayDeque())
                }
            }
        }

        val lines = input.iterator()
        var line = lines.next()
        while (line[1] != '1') {
            for (i in (1..line.length step 4)) {
                if (line[i].isWhitespace()) {
                    continue
                }
                val create = line[i]
                val which = (i - 1) / 4
                ensureSize(which + 1)
                stacks[which].addLast(create)
            }
            line = lines.next()
        }
        val last = line[line.length - 1].digitToInt()
        check(last == stacks.size)
        val instructions = mutableListOf<Instruction>()
        val pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)")
        while (lines.hasNext()) {
            line = lines.next()
            if (line.isBlank()) {
                continue
            }

            val match = pattern.matcher(line)
            check(match.matches()) { "no match for ${line}"}
            val times = match.group(1).toInt()
            val from = match.group(2).toInt()
            val to = match.group(3).toInt()
            instructions.add(Instruction(times, from, to))
        }

        return (stacks to instructions)
    }

    fun part1(input: List<String>): String {
        val (stacks, instructions) = parse(input)
        for (instruction in instructions) {
            for (i in 0 until instruction.times) {
                val crate = checkNotNull(stacks[instruction.from - 1].poll())
                stacks[instruction.to - 1].addFirst(crate)
            }
        }
        return stacks.map { it.peek() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val (stacks, instructions) = parse(input)
        for (instruction in instructions) {
            val holder = mutableListOf<Char>()
            for (i in 0 until instruction.times) {
                val crate = checkNotNull(stacks[instruction.from - 1].poll())
                holder.add(crate)
            }
            for (crate in holder.reversed()) {
                stacks[instruction.to - 1].addFirst(crate)
            }
        }
        return stacks.map { it.peek() }.joinToString("")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
