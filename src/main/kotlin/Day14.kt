import kotlin.math.sign

data class Position14(val right: Int, val down: Int) {

    fun to(end: Position14): Sequence<Position14> {
        var p = this
        return sequence {
            while (p != end) {
                yield(p)
                if (p.down == end.down) {
                    p = Position14((end.right - p.right).sign + p.right, p.down)
                } else if (p.right == end.right) {
                    p = Position14(p.right, (end.down - p.down).sign + p.down)
                }
            }
            yield(end)
        }
    }

    val below: Position14
        get() = Position14(right, down + 1)

    val belowLeft: Position14 get() = Position14(right - 1, down + 1)

    val belowRight: Position14 get() = Position14(right + 1, down + 1)
}

data class Bounds(var left: Int, var right: Int, var up: Int, var down: Int)

class Map14(private val grid: MutableMap<Position14, Char> = mutableMapOf()) {
    private var pen: Position14 = Position14(0, 0)
    private var bounds: Bounds = Bounds(500, 500, 0, 0)
    private var floor: Int? = null

    private fun update(pos: Position14) {
        bounds.left = min(bounds.left, pos.right)
        bounds.right = max(bounds.right, pos.right)
        bounds.up = min(bounds.up, pos.down)
        bounds.down = max(bounds.down, pos.down)
    }

    fun move(position: Position14) {
        pen = position
        update(pen)
    }

    fun draw(position: Position14) {
        for (p in pen.to(position)) {
            grid[p] = '#'
            update(p)
        }
        pen = position
    }

    fun drop(start: Position14): Boolean {
        var sand = start
        if (c(sand) != null) {
            return false
        }
        while (true) {
            sand = when {
                c(sand.below) == null -> sand.below
                c(sand.belowLeft) == null -> sand.belowLeft
                c(sand.belowRight) == null -> sand.belowRight
                else -> break
            }
            if (sand.down > bounds.down) {
                return false
            }
        }
        grid[sand] = 'o'
        return true
    }

    private fun c(pos: Position14): Char? {
        if (pos.down == floor) {
            return '#'
        }
        return grid[pos]
    }

    fun print() {
        for (y in bounds.up..bounds.down) {
            for (x in bounds.left..bounds.right) {
                val ch = grid[Position14(x, y)] ?: '.'
                print(ch)
            }
            println()
        }
    }

    fun addFloor() {
        bounds.down += 2
        floor = bounds.down
    }
}

fun main() {
    fun parse(input: List<String>): Map14 {
        val result = Map14()
        for (line in input) {
            val positions = line.split(" -> ").map {
                val (right, down) = it.split(',')
                Position14(right.toInt(), down.toInt())
            }
            result.move(positions[0])
            for (point in positions.drop(1)) {
                result.draw(point)
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val map = parse(input)
        var steps = 0
        while (map.drop(Position14(500, 0))) {
            steps += 1
        }
        map.print()
        return steps
    }


    fun part2(input: List<String>): Int {
        val map = parse(input)
        map.addFloor()
        var steps = 0
        while (map.drop(Position14(500, 0))) {
            steps += 1
        }
        map.print()
//        println(steps)
        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)

    val input = readInput("Day14")
    println(part1(input))

    check(part2(testInput) == 93)
    println(part2(input))
}
