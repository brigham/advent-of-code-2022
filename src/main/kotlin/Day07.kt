private val ElfDirectory.dirs: Sequence<ElfDirectory>
    get() = entries.asSequence().mapNotNull { it.asDirectory }

fun ElfDirectory.getDirectory(name: String): ElfDirectory =
    dirs.first { it.name == name }

interface ElfEntry {
    val name: String
    val size: Int
    val entries: List<ElfEntry>
    val asFile: ElfFile?
    val asDirectory: ElfDirectory?
}

data class ElfFile(override val name: String, override val size: Int, override val entries: List<ElfEntry> = listOf()) :
    ElfEntry {
    override val asFile: ElfFile = this
    override val asDirectory: ElfDirectory? = null
}

class ElfDirectory(
    override val name: String,
    val parent: ElfDirectory?,
    override val entries: MutableList<ElfEntry> = mutableListOf(),
) : ElfEntry {
    private var sizeCache: Int? = null

    private fun hasEntry(name: String): Boolean = entries.any { it.name == name }

    fun addDirectory(name: String) {
        check(!hasEntry(name))
        entries.add(ElfDirectory(name, this))
    }

    fun addFile(name: String, size: Int) {
        check(!hasEntry(name))
        entries.add(ElfFile(name, size))
    }

    fun walk(consumer: (dir: ElfDirectory) -> Unit) {
        val q = ArrayDeque<ElfDirectory>()
        q.add(this)
        while (q.isNotEmpty()) {
            val dir = q.removeFirst()
            consumer(dir)
            for (other in dir.dirs) {
                q.add(other)
            }
        }
    }

    override val size: Int
        get() {
            val curSizeCache = sizeCache
            return if (curSizeCache != null) curSizeCache else {
                val toCache = entries.sumOf { it.size }
                sizeCache = toCache
                return toCache
            }
        }
    override val asFile: ElfFile? = null
    override val asDirectory: ElfDirectory = this
}

class ElfFilesystem(var root: ElfDirectory = ElfDirectory("", null), var curdir: ElfDirectory? = null) {
    fun descend(name: String) {
        val newdir = curdir!!.getDirectory(name)
        curdir = newdir
    }

    fun ascend() {
        val newdir = curdir!!.parent
        curdir = newdir
    }

    fun gotoRoot() {
        curdir = root
    }
}

private const val TOTAL_SIZE = 70000000

private const val DESIRED_FREE_SPACE = 30000000

private const val PART1_MAX_SIZE = 100000

fun main() {
    fun parse(input: List<String>): ElfFilesystem {
        val iter = input.iterator()
        val fs = ElfFilesystem()
        var lsMode = false
        while (iter.hasNext()) {
            val line = iter.next().split(' ')
            when {
                line[0] == "$" -> {
                    lsMode = false
                    when (line[1]) {
                        "cd" -> when (line[2]) {
                            ".." -> fs.ascend()
                            "/" -> fs.gotoRoot()
                            else -> fs.descend(line[2])
                        }

                        "ls" -> lsMode = true
                    }
                }

                lsMode -> {
                    when (line[0]) {
                        "dir" -> fs.curdir!!.addDirectory(line[1])
                        else -> fs.curdir!!.addFile(line[1], line[0].toInt())
                    }
                }

                else -> {
                    error("Unexpected $line")
                }
            }
        }
        return fs
    }

    fun part1(input: List<String>): Int {
        val fs = parse(input)
        var result = 0
        fs.root.walk {
            val size = it.size
            if (size <= PART1_MAX_SIZE) {
                result += size
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val fs = parse(input)
        val rootSize = fs.root.size
        val freespace = TOTAL_SIZE - rootSize
        var result = rootSize
        fs.root.walk {
            val size = it.size
            if (size < result && size + freespace >= DESIRED_FREE_SPACE) {
                result = size
            }
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
