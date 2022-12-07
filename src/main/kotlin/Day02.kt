enum class Outcome {
    Loss, Draw, Win;

    val score: Int
        get() = this.ordinal * 3

    fun against(opp: Piece): Piece {
        return when (this) {
            Loss -> opp.beats
            Draw -> opp
            Win -> opp.beatenBy
        }
    }
}

enum class Piece {
    Rock {
        override val beats: Piece
            get() = Scissors
        override val beatenBy: Piece
            get() = Paper
    },
    Paper {
        override val beats: Piece
            get() = Rock
        override val beatenBy: Piece
            get() = Scissors
    },
    Scissors {
        override val beats: Piece
            get() = Paper
        override val beatenBy: Piece
            get() = Rock
    };

    abstract val beats: Piece

    abstract val beatenBy: Piece

    val score: Int = ordinal + 1

    fun play(other: Piece): Outcome {
        return when (other) {
            this -> Outcome.Draw
            this.beats -> Outcome.Loss
            this.beatenBy -> Outcome.Win
            else -> error("Someone added a new piece.")
        }
    }
}

val pieces = mapOf(
    "A" to Piece.Rock, "X" to Piece.Rock,
    "B" to Piece.Paper, "Y" to Piece.Paper,
    "C" to Piece.Scissors, "Z" to Piece.Scissors
)
val outcomes = mapOf("X" to Outcome.Loss, "Y" to Outcome.Draw, "Z" to Outcome.Win)

fun main() {
    fun part1(input: List<String>): Int {
        var score = 0
        for (line in input) {
            val (oppC, meC) = line.split(' ')
            val opp = pieces[oppC]!!
            val me = pieces[meC]!!
            score += opp.play(me).score + me.score
        }
        return score
    }

    fun part2(input: List<String>): Int {
        var score = 0
        for (line in input) {
            val (oppC, outcomeC) = line.split(' ')
            val opp = pieces[oppC]!!
            val outcome = outcomes[outcomeC]!!
            score += outcome.score + outcome.against(opp).score
        }
        return score
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
