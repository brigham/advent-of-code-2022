val rocks = listOf(
    listOf(15),
    listOf(2, 7, 2),
    listOf(7, 4, 4),
    listOf(1, 1, 1, 1),
    listOf(3, 3)
)

fun rockMachine(): Sequence<List<Int>> = sequence {
    while (true) {
        yieldAll(rocks)
    }
}

fun main() {
    fun parse(line: String): Sequence<IndexedValue<Char>> {
        val chars = line.toList()
        return sequence {
            while (true) {
                yieldAll(chars.withIndex())
            }
        }
    }

    fun parse2(line: String): List<Char> {
        return line.toList()
    }

    fun render(grid: List<Int>, row: Int, rock: List<Int>, shift: Int) {
        for (curr in (row + rock.size) downTo 0) {
            print("|")
            for (i in 0 until 7) {
                var cell = '.'
                val bit = 1 shl i
                if (curr in row until (row + rock.size)) {
                    if ((rock[(curr - row)] shl shift) and bit != 0) cell = '@'
                }
                if (grid.size > curr && grid[curr] and bit != 0) cell = '#'
                print(cell)
            }
            println("|")
        }
        println("+-------+\n")
    }

    fun run(input: List<String>, rockCount: Long): Long {
        val commands = parse(input[0])
        val rockIter = rockMachine().iterator()
        val commandIter = commands.iterator()
        val grid = ArrayDeque<Int>()
//        var moves = 0
        var dropped = 0L
        var drops = 0L

        for (i in 0 until rockCount) {
            val rock = rockIter.next()
            var row = grid.size + 3
            var shift = 2
//            if (moves < 10) {
//                println()
//                render(grid, row, rock, shift)
//                moves += 1
//            }
            while (true) {
                val oldShift = shift
                val cmd = commandIter.next().value
//                if (moves < 10) print(cmd)
                when (cmd) {
                    '<' -> shift -= 1
                    '>' -> shift += 1
                }
                if (shift < 0) {
                    shift = 0
                }
                for (line in rock) {
                    if ((line shl shift) and (1 shl 7) != 0) {
                        shift -= 1
                    }
                }
                for ((idx, line) in rock.withIndex()) {
                    if (grid.size <= row + idx) {
                        break
                    }
                    if (grid[row + idx] and (line shl shift) != 0) {
                        shift = oldShift
                        break
                    }
                }
                var place = false
                for ((idx, line) in rock.withIndex()) {
                    if (row + idx == 0) {
                        place = true
                        break
                    }
                    if (grid.size <= row + idx - 1) {
                        break
                    }
                    if (grid[row + idx - 1] and (line shl shift) != 0) {
                        place = true
                        break
                    }
                }
                if (place) {
                    while (grid.size < (row + rock.size)) {
                        grid.add(0)
                    }
                    for ((idx, line) in rock.withIndex()) {
                        grid[row + idx] = grid[row + idx] or (line shl shift)
                    }
                    for ((idx, _) in rock.withIndex().reversed()) {
                        if (grid[row + idx] == 0x7F) {
                            drops += 1
                            for (z in 0..(row + idx)) {
                                grid.removeFirst()
                                dropped += 1
                            }
                            break
                        }
                    }
                    break
                }
                row -= 1
            }
        }
        println("drops: $drops, dropped: $dropped")
        return grid.size.toLong() + dropped
    }

    fun part1(input: List<String>): Long {
        return run(input, 2022)
    }

    fun part2(input: List<String>): Long {
        println(parse2(input[0]).size)
        return 0 // run(input, 1000000000000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 3068L)

    val input = readInput("Day17")
    println(part1(input))

    check(part2(testInput) == 0L) // 1514285714288)
    println("starting")
    println(part2(input))
}
