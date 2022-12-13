data class Position12(val r: Int, val c: Int)

data class HeightMap(val map: List<List<Char>>, val start: Position12, val end: Position12)

data class Node(val steps: Int, val position12: Position12)
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
        val q = ArrayDeque<Node>()
        val seen = mutableSetOf<Position12>()
        q.add(Node(0, map.start))
        while (q.isNotEmpty()) {
            val next = q.removeFirst()
            if (next.position12 in seen) {
                continue
            }
            seen.add(next.position12)
            if (next.position12 == map.end) {
                return next.steps
            }
            val row = next.position12.r
            val col = next.position12.c
            val height = map.map[row][col]
            fun check(r2: Int, c2: Int) {
                if (r2 in map.map.indices && c2 in map.map[r2].indices) {
                    val newHeight = map.map[r2][c2]
                    if (newHeight - height <= 1) {
                        q.add(Node(next.steps + 1, Position12(r2, c2)))
                    }
                }
            }
            check(row - 1, col)
            check(row + 1, col)
            check(row, col - 1)
            check(row, col + 1)
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        val map = parse(input)
        val q = ArrayDeque<Node>()
        val seen = mutableSetOf<Position12>()
        q.add(Node(0, map.end))
        while (q.isNotEmpty()) {
            val next = q.removeFirst()
            if (next.position12 in seen) {
                continue
            }
            seen.add(next.position12)
            if (map.map[next.position12.r][next.position12.c] == 'a') {
                return next.steps
            }
            val row = next.position12.r
            val col = next.position12.c
            val height = map.map[row][col]
            fun check(r2: Int, c2: Int) {
                if (r2 in map.map.indices && c2 in map.map[r2].indices) {
                    val newHeight = map.map[r2][c2]
                    if (newHeight - height >= -1) {
                        q.add(Node(next.steps + 1, Position12(r2, c2)))
                    }
                }
            }
            check(row - 1, col)
            check(row + 1, col)
            check(row, col - 1)
            check(row, col + 1)
        }
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
