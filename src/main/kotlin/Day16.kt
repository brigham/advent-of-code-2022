val pattern16 = Regex("""Valve (.*) has flow rate=(\d+); tunnels? leads? to valves? (.*)""")

data class Node16(val name: String, val flowRate: Int, val edges: List<String>)

data class State16(val location: String, val timeLeft: Int, val open: Set<String>, val pressureReleased: Int)

interface PairGuy {
    fun <E> get(pair: Pair<E, E>): E

    fun <E> other(pair: Pair<E, E>): E

    fun <E> set(pair: Pair<E, E>, value: E): Pair<E, E>
}

object FirstPairGuy: PairGuy {
    override fun <E> get(pair: Pair<E, E>): E {
        return pair.first
    }

    override fun <E> other(pair: Pair<E, E>): E {
        return pair.second
    }

    override fun <E> set(pair: Pair<E, E>, value: E): Pair<E, E> {
        return value to pair.second
    }
}

object SecondPairGuy: PairGuy {
    override fun <E> get(pair: Pair<E, E>): E {
        return pair.second
    }

    override fun <E> other(pair: Pair<E, E>): E {
        return pair.first
    }

    override fun <E> set(pair: Pair<E, E>, value: E): Pair<E, E> {
        return pair.first to value
    }
}

data class PairState16(
    val location: Pair<String, String>,
    val timeLeft: Pair<Int, Int>,
    val open: Set<String>,
    val pressureReleased: Int
)

fun main() {
    fun parse(input: List<String>): Map<String, Node16> =
        input.map { pattern16.matchEntire(it) ?: error("no match for $it") }
            .map { it.groupValues }
            .map { Node16(it[1], it[2].toInt(), it[3].split(", ")) }
            .associateBy { it.name }

    fun part1(input: List<String>): Int {
        val nodes = parse(input)
        val goodNodes = nodes.filter { it.value.flowRate > 0 }.keys
        var bestScore = 0
        val cache = mutableMapOf<Pair<String, String>, Int>()
        val stack = ArrayDeque<State16>()
        stack.add(State16("AA", 30, setOf(), 0))
        while (stack.isNotEmpty()) {
            val nextNode = stack.removeLast()
            val from = nextNode.location
            for (to in (goodNodes - nextNode.open)) {
                val steps = cache[from to to] ?: bfs(
                    from,
                    next = { nodes[it]!!.edges.asSequence() },
                    found = { it == to })!!.steps
                cache[from to to] = steps
                val newTimeLeft = nextNode.timeLeft - steps
                if (newTimeLeft > 0) {
                    val score = nextNode.pressureReleased + nodes[to]!!.flowRate * (newTimeLeft - 1)
                    stack.add(
                        State16(
                            to, newTimeLeft - 1, nextNode.open + setOf(to),
                            score
                        )
                    )
                    bestScore = max(score, bestScore)
                }
            }
        }

        return bestScore
    }

    fun part2(input: List<String>): Int {
        val nodes = parse(input)
        val goodNodes = nodes.filter { it.value.flowRate > 0 }.keys
        var bestScore = 0
        val cache = mutableMapOf<Pair<String, String>, Int>()
        val stack = ArrayDeque<PairState16>()
        val seen = mutableMapOf<Set<String>, Int>()
        stack.add(PairState16("AA" to "AA", 26 to 26, setOf(), 0))
        while (stack.isNotEmpty()) {
            val nextNode = stack.removeLast()
            if (nextNode.pressureReleased <= (seen[nextNode.open] ?: -1)) {
               continue
            } else {
                seen[nextNode.open] = nextNode.pressureReleased
            }
            for (pairGuy in listOf(FirstPairGuy, SecondPairGuy)) {
                val from = pairGuy.get(nextNode.location)
                for (to in (goodNodes - nextNode.open)) {
                    val steps = cache[from to to] ?: bfs(
                        from,
                        next = { nodes[it]!!.edges.asSequence() },
                        found = { it == to })!!.steps
                    cache[from to to] = steps
                    val newTimeLeft = pairGuy.get(nextNode.timeLeft) - steps
                    if (newTimeLeft > 0) {
                        val score = nextNode.pressureReleased + nodes[to]!!.flowRate * (newTimeLeft - 1)
                        stack.add(
                            PairState16(
                                pairGuy.set(nextNode.location, to),
                                pairGuy.set(nextNode.timeLeft, newTimeLeft - 1),
                                nextNode.open + setOf(to),
                                score
                            )
                        )
                        bestScore = max(score, bestScore)
                    }
                }
            }
        }
        return bestScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    val input = readInput("Day16")
    println(part1(input))

    check(part2(testInput) == 1707)
    println(part2(input))
}
