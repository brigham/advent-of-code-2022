import kotlin.math.absoluteValue

val pattern15 = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")

data class Position15(val x: Int, val y: Int) {
    fun distance(other: Position15): Int {
        return (x - other.x).absoluteValue + (y - other.y).absoluteValue
    }
}

data class Report(val sensor: Position15, val beacon: Position15) {
    val distance: Int
        get() = sensor.distance(beacon)

    fun minX(y: Int): Int? {
        if (!coversRow(y)) {
            return null
        }
        var dist = distance
        dist -= (sensor.y - y).absoluteValue
        return sensor.x - dist
    }

    fun maxX(y: Int): Int? {
        if (!coversRow(y)) {
            return null
        }
        var dist = distance
        dist -= (sensor.y - y).absoluteValue
        return sensor.x + dist
    }

    fun coversRow(y: Int): Boolean {
        val dist = distance
        return y in ((sensor.y - dist)..(sensor.y + dist))
    }

}

fun main() {
    fun parse(line: String): Report {
        val match = pattern15.matchEntire(line)!!
        return Report(Position15(match.groupValues[1].toInt(), match.groupValues[2].toInt()),
            Position15(match.groupValues[3].toInt(), match.groupValues[4].toInt()))
    }

    fun parse(input: List<String>): List<Report> {
        return input.map { parse(it) }
    }

    fun covered(reports: List<Report>, y: Int, excludeBeacons: Boolean = true): MutableSet<Int> {
        val covered = mutableSetOf<Int>()
        for (report in reports) {
            val start = report.minX(y) ?: continue
            val end = report.maxX(y) ?: continue
            if (excludeBeacons) {
                for (i in (start..end)) {
                    if (y == report.beacon.y && i == report.beacon.x) {
                        continue
                    }
                    covered.add(i)
                }
            } else {
                covered.addAll(start..end)
            }
        }
        return covered
    }

    fun covered2(reports: List<Report>, y: Int): MutableSet<IntRange> {
        val covered = mutableSetOf<IntRange>()
        for (report in reports) {
            val start = report.minX(y) ?: continue
            val end = report.maxX(y) ?: continue
            val overlapping = covered.filter { it.overlap(start..end) != null }
            if (overlapping.isEmpty()) {
                covered.add(start..end)
            } else {
                covered.removeAll(overlapping.toSet())
                covered.add(min(overlapping.minOf { it.first }, start)..max(overlapping.maxOf { it.last }, end))
            }
        }
        return covered
    }

    fun part1(input: List<String>, y: Int): Int {
        val reports = parse(input)
        return covered(reports, y).size
    }

    fun part2(input: List<String>, limit: Int): Long {
        val reports = parse(input)
        for (y in (0..limit)) {
            val co = covered2(reports, y)
            co.trim(0, limit)
            co.add(-1..-1)
            co.add((limit+1)..(limit+1))
            if (co.size > 1) {
                val closed = co.sortedBy { it.first }.zipWithNext { a: IntRange, b: IntRange ->
                    val gap = (a.last + 1) until b.first
                    return@zipWithNext if (gap.isEmpty()) {
                        null
                    } else { gap }
                }.filterNotNull()
                if (closed.isNotEmpty()) {
                    val x = closed.first().first
                    println("$x, $y, $closed, ${x * 4000000 + y}")
                    return x.toLong() * 4000000L + y.toLong()
                }
            }
        }
        return -1L
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)

    val input = readInput("Day15")
    println(part1(input, 2000000))

    check(part2(testInput, 20) == 56000011L)
    println(part2(input, 4000000)) // 698766328
}

private fun MutableSet<IntRange>.trim(lowest: Int, highest: Int) {
    val i = iterator()
    val toAdd = mutableSetOf<IntRange>()
    while (i.hasNext()) {
        val range = i.next()
        if (range.last < lowest) {
            i.remove()
            continue
        }
        if (range.first > highest) {
            i.remove()
            continue
        }
        if (range.first < lowest || range.last > highest) {
            val newRange = max(range.first, lowest)..min(range.last, highest)
            toAdd.add(newRange)
            i.remove()
            continue
        }
    }
    addAll(toAdd)
}
