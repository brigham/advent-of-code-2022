data class Position12(val r: Int, val c: Int)

data class HeightMap(val map: List<List<Char>>, val start: Position12, val end: Position12)

fun main() {
    fun parse(input: List<String>): HeightMap {
        var start: Position12? = null
        var end: Position12? = null
        for (rowIdx in input.indices) {
            val row = input[rowIdx]
            for (colIdx in row.indices) {
                when (row[colIdx]) {
                    'S' -> start = Position12(rowIdx, colIdx)
                    'E' -> end = Position12(rowIdx, colIdx)
                }
            }
        }
        val map = input.map { it.toMutableList() }
        map[start!!.r][start.c] = 'a'
        map[end!!.r][end.c] = 'z'
        return HeightMap(map.map { it.toList() }, start, end)
    }

    fun part1(input: List<String>): Int {
        val map = parse(input)
        return bfs(map.start, next = { p ->
            sequence {
                val row = p.r
                val col = p.c
                val height = map.map[row][col]
                fun check(r2: Int, c2: Int): Position12? {
                    if (r2 in map.map.indices && c2 in map.map[r2].indices) {
                        val newHeight = map.map[r2][c2]
                        if (newHeight - height <= 1) {
                            return Position12(r2, c2)
                        }
                    }
                    return null
                }
                check(row - 1, col)?.let { yield(it) }
                check(row + 1, col)?.let { yield(it) }
                check(row, col - 1)?.let { yield(it) }
                check(row, col + 1)?.let { yield(it) }
            }
        }, found = { p -> p == map.end })?.steps ?: -1
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        return bfs(map.end, next = { p ->
            sequence {
                val row = p.r
                val col = p.c
                val height = map.map[row][col]
                fun check(r2: Int, c2: Int): Position12? {
                    if (r2 in map.map.indices && c2 in map.map[r2].indices) {
                        val newHeight = map.map[r2][c2]
                        if (newHeight - height >= -1) {
                            return Position12(r2, c2)
                        }
                    }
                    return null
                }
                check(row - 1, col)?.let { yield(it) }
                check(row + 1, col)?.let { yield(it) }
                check(row, col - 1)?.let { yield(it) }
                check(row, col + 1)?.let { yield(it) }
            }
        }, found = { p -> map.map[p.r][p.c] == 'a' })?.steps ?: -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
