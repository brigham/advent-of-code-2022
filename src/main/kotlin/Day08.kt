fun main() {
    fun parseLine(line: String): List<Int> =
        line.toList().map { it.digitToInt() }.toList()

    fun parse(input: List<String>): List<List<Int>> = input.map { parseLine(it) }

    fun part1(input: List<String>): Int {
        val heights = parse(input)
        val visible = mutableListOf<MutableList<Boolean>>()
        for (row in heights) {
            val newrow = mutableListOf<Boolean>()
            for (tree in row) {
                newrow.add(false)
            }
            visible.add(newrow)
        }

        for (rowIdx in heights.indices) {
            val row = heights[rowIdx]
            var shortest = -1
            for (fromLeftIdx in row.indices) {
                if (row[fromLeftIdx] > shortest) {
                    visible[rowIdx][fromLeftIdx] = true
                    shortest = row[fromLeftIdx]
                }
            }
            shortest = -1
            for (fromRightIdx in row.indices.reversed()) {
                if (row[fromRightIdx] > shortest) {
                    visible[rowIdx][fromRightIdx] = true
                    shortest = row[fromRightIdx]
                }
            }
        }
        for (colIdx in heights[0].indices) {
            var shortest = -1
            for (fromTopIdx in heights.indices) {
                if (heights[fromTopIdx][colIdx] > shortest) {
                    visible[fromTopIdx][colIdx] = true
                    shortest = heights[fromTopIdx][colIdx]
                }
            }
            shortest = -1
            for (fromBottomIdx in heights.indices.reversed()) {
                if (heights[fromBottomIdx][colIdx] > shortest) {
                    visible[fromBottomIdx][colIdx] = true
                    shortest = heights[fromBottomIdx][colIdx]
                }
            }
        }
        return visible.flatten().count { it }
    }

    fun part2(input: List<String>): Int {
        val heights = parse(input)
        val scores = mutableListOf<MutableList<Int>>()
        for (row in heights) {
            val newrow = mutableListOf<Int>()
            for (tree in row) {
                newrow.add(0)
            }
            scores.add(newrow)
        }

        fun viewingDistance(rowIdx: Int, colIdx: Int): Int {
            val height = heights[rowIdx][colIdx]
            var score = 1
            var inc = 0
            var movingUp = rowIdx - 1
            while (movingUp >= 0) {
                inc++
                if (heights[movingUp][colIdx] >= height) {
                    break
                }
                movingUp--
            }
            score *= inc
            inc = 0
            var movingDown = rowIdx + 1
            while (movingDown <= heights.lastIndex) {
                inc++
                if (heights[movingDown][colIdx] >= height) {
                    break
                }
                movingDown++
            }
            score *= inc
            inc = 0
            var movingLeft = colIdx - 1
            while (movingLeft >= 0) {
                inc++
                if (heights[rowIdx][movingLeft] >= height) {
                    break
                }
                movingLeft--
            }
            score *= inc
            inc = 0
            var movingRight = colIdx + 1
            while (movingRight <= heights[0].lastIndex) {
                inc++
                if (heights[rowIdx][movingRight] >= height) {
                    break
                }
                movingRight++
            }
            score *= inc
            return score
        }

        for (rowIdx in heights.indices.drop(1).dropLast(1)) {
            for (colIdx in heights[rowIdx].indices.drop(1).dropLast(1)) {
                scores[rowIdx][colIdx] = viewingDistance(rowIdx, colIdx)
            }
        }

        return scores.flatten().max()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
