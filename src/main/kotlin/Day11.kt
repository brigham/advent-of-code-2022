import java.math.BigInteger

val pattern = Regex(
    """Monkey (\d):
 {2}Starting items: (.*)
 {2}Operation: new = old ([+*]) (old|\d+)
 {2}Test: divisible by (.*)
 {4}If true: throw to monkey (.*)
 {4}If false: throw to monkey (.*)"""
)

enum class Operator {
    PLUS {
        override fun apply(a: BigInteger, b: BigInteger): BigInteger {
            return a + b
        }
    },
    TIMES {
        override fun apply(a: BigInteger, b: BigInteger): BigInteger {
            return a * b
        }
    };

    abstract fun apply(a: BigInteger, b: BigInteger): BigInteger
}

sealed class Operand {
    abstract fun value(op: BigInteger): BigInteger
}

object SelfOperand : Operand() {
    override fun value(op: BigInteger) = op
}

data class IntOperand(val v: BigInteger) : Operand() {
    override fun value(op: BigInteger) = v
}

data class Monkey(
    val which: Int,
    val items: MutableList<BigInteger>,
    val operator: Operator,
    val operand: Operand,
    val divCheck: BigInteger,
    val ifTrue: Int,
    val ifFalse: Int,
    var score: BigInteger = BigInteger.ZERO
)

fun main() {
    fun parse(input: String): List<Monkey> {
        val result = mutableListOf<Monkey>()
        for (match in pattern.findAll(input)) {
            val g = match.groups
            result.add(
                Monkey(
                    g[1]!!.value.toInt(), g[2]!!.value.split(", ").map { it.toBigInteger() }.toMutableList(),
                    when (g[3]!!.value) {
                        "*" -> Operator.TIMES
                        "+" -> Operator.PLUS
                        else -> error("huh")
                    },
                    when (g[4]!!.value) {
                        "old" -> SelfOperand
                        else -> IntOperand(g[4]!!.value.toBigInteger())
                    },
                    g[5]!!.value.toBigInteger(), g[6]!!.value.toInt(), g[7]!!.value.toInt()
                )
            )

        }
        return result
    }

    fun part1(input: String): BigInteger {
        val monkeys = parse(input).toMutableList()
        println(monkeys)
        for (round in 0 until 20) {
            for (monkeyIdx in monkeys.indices) {
                val monkey = monkeys[monkeyIdx]
                for (item in monkey.items) {
                    val newWorry = (monkey.operator.apply(item, monkey.operand.value(item))) / BigInteger.valueOf(3L)
                    val check = newWorry % monkey.divCheck
                    val passTo = if (check == BigInteger.ZERO) monkey.ifTrue else monkey.ifFalse
                    monkeys[passTo].items.add(newWorry)
                }
                monkeys[monkeyIdx].score += monkey.items.size.toBigInteger()
                monkeys[monkeyIdx].items.clear()
            }
        }
        println(monkeys)
        val heap = descendingHeap<BigInteger>()
        heap.addAll(monkeys.map { it.score })
        println(heap)
        return heap.poll() * heap.poll()
    }

    fun part2(input: String): BigInteger {
        val monkeys = parse(input).toMutableList()
        val divisor = monkeys.map { it.divCheck }.reduce { a, b -> a * b }
        println(monkeys)
        for (round in 0 until 10000) {
            for (monkeyIdx in monkeys.indices) {
                val monkey = monkeys[monkeyIdx]
                for (item in monkey.items) {
                    val newWorry = (monkey.operator.apply(item, monkey.operand.value(item))) % divisor
                    val check = newWorry % monkey.divCheck
                    val passTo = if (check == BigInteger.ZERO) monkey.ifTrue else monkey.ifFalse
                    monkeys[passTo].items.add(newWorry)
                }
                monkeys[monkeyIdx].score += monkey.items.size.toBigInteger()
                monkeys[monkeyIdx].items.clear()
            }
        }
        println(monkeys)
        val heap = descendingHeap<BigInteger>()
        heap.addAll(monkeys.map { it.score })
        println(heap)
        val result = heap.poll() * heap.poll()
        println(result)
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = slurp("Day11_test")
    check(part1(testInput) == BigInteger.valueOf(10605L))
    check(part2(testInput) == BigInteger.valueOf(2713310158))

    val input = slurp("Day11")
    println(part1(input))
    println(part2(input))
}
