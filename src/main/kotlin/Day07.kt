interface SizeHaver {
    val size: Int
}

data class ElfFile(val name: String, override val size: Int) : SizeHaver

class ElfDirectory(val name: String, val parent: ElfDirectory?, val dirs: MutableList<ElfDirectory> = mutableListOf(), val files: MutableList<ElfFile> = mutableListOf()) : SizeHaver {
    fun addDirectory(name: String) {
        dirs.add(ElfDirectory(name, this))
    }

    fun getDirectory(name: String): ElfDirectory {
        return dirs.filter { it.name == name }.first()
    }

    fun addFile(name: String, size: Int) {
        files.add(ElfFile(name, size))
    }

    override val size: Int
        get() {
            var result = 0
            for (dir in dirs) {
                result += dir.size
            }
            for (file in files) {
                result += file.size
            }
            return result
        }


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

fun main() {
    fun parse(input: List<String>): ElfFilesystem {
        val iter = input.iterator()
        val fs = ElfFilesystem()
        var lsMode = false
        while (iter.hasNext()) {
            val line = iter.next().split(' ')
            if (line[0] == "$") {
                lsMode = false
                when (line[1]) {
                    "cd" -> when (line[2]) {
                        ".." -> fs.ascend()
                        "/" -> fs.gotoRoot()
                        else -> fs.descend(line[2])
                    }
                    "ls" -> lsMode = true
                }
            } else if (lsMode) {
                when (line[0]) {
                    "dir" -> fs.curdir!!.addDirectory(line[1])
                    else -> fs.curdir!!.addFile(line[1], line[0].toInt())
                }
            } else {
                error("Unexpected $line")
            }
        }
        return fs
    }

    fun part1(input: List<String>): Int {
        val fs = parse(input)
        val q = ArrayDeque<ElfDirectory>()
        q.add(fs.root)
        var result = 0
        while (q.isNotEmpty()) {
            val dir = q.removeFirst()
            val size = dir.size
            if (size <= 100000) {
                result += size
            }
            for (other in dir.dirs) {
                q.add(other)
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val fs = parse(input)
        val rootSize = fs.root.size
        val freespace = 70000000 - rootSize
        val q = ArrayDeque<ElfDirectory>()
        q.add(fs.root)
        var result = rootSize
        while (q.isNotEmpty()) {
            val dir = q.removeFirst()
            val size = dir.size
            if (size < result && size + freespace >= 30000000) {
                result = size
            }
            for (other in dir.dirs) {
                q.add(other)
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
