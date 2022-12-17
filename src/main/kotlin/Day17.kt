val rocks = listOf(
    listOf(15),
    listOf(2, 7, 2),
    listOf(7, 4, 4),
    listOf(1, 1, 1, 1),
    listOf(3, 3)
)

fun rockMachine(): Sequence<IndexedValue<List<Int>>> = sequence {
    while (true) {
        yieldAll(rocks.withIndex())
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
        for (curr in (row + rock.size - 1) downTo row) {
            print("|")
            for (i in 0 until 7) {
                var cell = '.'
                val bit = 1 shl i
                if (grid.size > curr && grid[curr] and bit != 0) cell = '#'
                if (curr in row until (row + rock.size)) {
                    if ((rock[(curr - row)] shl shift) and bit != 0) cell = '@'
                }
                print(cell)
            }
            println("|")
        }
        println()
//        println("+-------+\n")
    }

    fun shiftIt3(rockIdx: Int, currShift: Int, cmd: Char): Int {
        if (cmd == '<') {
            return if (currShift > 0) currShift - 1 else currShift
        }
        return when (rockIdx) {
            0 -> when (currShift) {
                in 0..2 -> currShift + 1
                else -> currShift
            }
            1 -> when (currShift) {
                in 0..3 -> currShift + 1
                else -> currShift
            }
            2 -> when (currShift) {
                in 0..3 -> currShift + 1
                else -> currShift
            }
            3 -> when (currShift) {
                in 0..5 -> currShift + 1
                else -> currShift
            }
            4 -> when (currShift) {
                in 0..4 -> currShift + 1
                else -> currShift
            }
            else -> error("no such rock")
        }
    }

    fun blocked(grid: List<Int>, row: Int): Boolean {
        if (row + 4 > grid.size) return false
        var cov = 0
        for (ir in row until row + 4) {
            cov = cov or grid[ir]
        }

        if (cov == 0x7f) return true
        return false
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
            val (rockIdx, rock) = rockIter.next()
            var row = grid.size + 3
            var shift = 2
//            if (moves < 10) {
//                println()
//                render(grid, row, rock, shift)
//                moves += 1
//            }
            while (true) {
                val oldShift = shift
                val (cmdIdx, cmd) = commandIter.next()
                shift = shiftIt3(rockIdx, shift, cmd)
//                if (moves < 10) print(cmd)

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

                    for (idx in (rock.size - 1) downTo -3) {
                        if (row + idx < 0) break
                        if (grid[row + idx] == 0x7F || blocked(grid, row + idx)) {
//                            render(grid, row, rock, shift)

                            drops += 1
//                            if (cmdIdx == xyz) {
//                                println("$i, $rockIdx, $cmdIdx: ${grid.size.toLong() + dropped}")
//                            }
                            for (z in 0..(row + idx)) {
                                grid.removeFirst()
                                dropped += 1
                            }
                            break
                        }
                    }

//                    if (i == ixyz || i == (ixyz + extraxyz - 1)) {
//                        println("$i, $rockIdx, $cmdIdx: ${grid.size.toLong() + dropped}")
//                    }

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
        // Requires some manual work:
        // 1. Uncomment the line under cmdIdx == xyz. Run and identify a cmdIdx that repeats.
        // 2. Uncomment the whole if block and replace xyz with your cmdIdx.
        // 3. Get the difference in i between two rows (double check that it repeats). That's how many rocks between
        //    cycles.
        // 4. Get the difference in units (after the colon) between two rows.
        // 5. Compute floor((1e12 - i) / cycle_length) * unit_growth_per_cycle + units_at_rock_i
        // 6. Determine how many extra cycles there are: (1e12 - i) - floor(((1e12 - i) / cycle_length) * cycle_length
        // 7. Comment out the if block and uncomment the if block with extraxyz, replacing ixyz with your i and
        //    extraxyz with the result of step 6.
        // 8. Get the difference in units and add to the result of 5. That's the answer.
        println(parse2(input[0]).size)
        val run = run(input, 1000000000000)

        println(run)

        return run
    }

    fun width(rock: List<Int>): Int {
        fun bitRange(i: Int): IntRange {
            var ii = i
            var low = 0
            while (ii and 1 == 0) {
                low += 1
                ii = ii shr 1
            }
            var high = low
            ii = ii shr 1
            while (ii != 0) {
                high += 1
                ii = ii shr 1
            }
            return low..high
        }
        return rock.maxOf { bitRange(it).count() }
    }

    fun writeShiftIt3() {
        val widths = rocks.map { width(it) }
        println("fun shiftIt3(rockIdx: Int, currShift: Int, cmd: Char): Int {")
        println("  if (cmd == '<' && currShift > 0) return currShift - 1")
        println("  return when (rockIdx) {")
        for ((index, width) in widths.withIndex()) {
            println("    $index -> when (currShift) {")
            println("      in 0..${6 - width} -> currShift + 1")
            println("      else -> currShift")
            println("    }")
        }
        println("""  else -> error("no such rock")""")
        println("  }")
        println("}")
    }

//    writeShiftIt3()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
//    check(part1(testInput) == 3068L)

    val input = readInput("Day17")
//    println(part1(input))

    println("Part 2")
//    check(part2(testInput) == 1514285714288)
    println("starting")
    println(part2(input))
}
