sealed class Ins

object NoopIns: Ins()

data class AddxIns(val add: Int): Ins()

fun main() {
    fun parse(input: List<String>): List<Ins> = input.map { it.split(" ") }
        .map {
            when (it[0]) {
                "noop" -> NoopIns
                "addx" -> AddxIns(it[1].toInt())
                else -> error("Unknown ${it[0]}")
            }
        }.toList()

    fun part1(input: List<String>, verbose: Boolean): Int {
        val inx = parse(input)
        println(inx.filterIsInstance<AddxIns>().sumOf { it.add })
        var x = 1
        var tick = 0
        val inxIter = inx.iterator()
        var ctr = 1
        var ins: Ins? = null
        var sum = 0
        while (inxIter.hasNext() || tick != 0) {
            if ((ctr - 20) % 40 == 0) {
                println("$x * $ctr == ${x * ctr}")
                sum += (x * ctr)
            }
            if (tick == 1) {
                tick = 0
                x += when (ins) {
                    is NoopIns -> error("impossible")
                    is AddxIns -> ins.add
                    null -> error("impossible")
                }
            } else {
                ins = inxIter.next()
                when (ins) {
                    is NoopIns -> {}
                    is AddxIns -> {
                        tick = 1
                    }
                }
            }
            if (verbose) {
                println("ctr: $ctr, x: $x, ins: $ins")
            }

            ctr++
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val inx = parse(input)
        var x = 1
        var tick = 0
        val inxIter = inx.iterator()
        var ctr = 1
        var ins2: Ins? = null
        while (inxIter.hasNext() || tick != 0) {
            val pixel = (ctr - 1) % 40
            if (x in setOf(pixel - 1, pixel, pixel + 1)) {
                print("#")
            } else {
                print(".")
            }
            if (pixel == 39) {
                println()
            }
            if (tick == 1) {
                tick = 0
                x += when (ins2) {
                    is NoopIns -> error("impossible")
                    is AddxIns -> ins2.add
                    null -> error("impossible")
                }
            } else {
                ins2 = inxIter.next()
                when (ins2) {
                    is NoopIns -> {}
                    is AddxIns -> {
                        tick = 1
                    }
                }
            }

            ctr++
        }
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(readInput("Day10_simple"), verbose=false) == 0)
    check(part1(testInput, true) == 13140)
    check(part2(testInput) == 0)

    val input = readInput("Day10")
    println(part1(input, false))
    println(part2(input))
}
