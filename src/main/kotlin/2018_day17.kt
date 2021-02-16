// https://adventofcode.com/2018/day/17
import java.util.*

typealias Field = MutableList<MutableList<Char>>

data class Segment(val pos: Int, val range: IntRange, val isVertical: Boolean)

fun MatchGroupCollection.getInt(name: String) = get(name)!!.value.toInt()

var minX = 0
var maxX = 0
var minY = 0
var maxY = 0

fun print(field: Field) {
    for (i in 1..5)
        println()

    for (y in field[0].indices) {
        for (x in minX..maxX) {
            print(field[x][y])
        }
        println()
    }
    Thread.sleep(100)
}

fun simulate(field: Field, x: Int, y: Int) {
    //print(field)
    val queue = LinkedList<Pair<Int, Int>>()
    queue.push(x to y)
    while (!queue.isEmpty()) {
        val (x, y) = queue.pop()
        if (field[x][y] == '|' && y + 1 < field[x].size) {
            when (field[x][y + 1]) {
                '.' -> {
                    field[x][y + 1] = '|'
                    queue.push(x to y + 1)
                }
                '#', '~' -> {
                    if (x - 1 >= 0 && field[x - 1][y] == '.') {
                        field[x - 1][y] = '|'
                        queue.push(x - 1 to y)
                    }
                    if (x + 1 < field.size && field[x + 1][y] == '.') {
                        field[x + 1][y] = '|'
                        queue.push(x + 1 to y)
                    }
                    val leftBorder = (x - 1 downTo 0).find { field[it][y] !in setOf('|', '~') }
                    val rightBorder = (x + 1 until field.size).find { field[it][y] !in setOf('|', '~') }
                    if (leftBorder != null && field[leftBorder][y] == '#' && rightBorder != null && field[rightBorder][y] == '#' &&
                        (leftBorder + 1 until rightBorder).all { field[it][y + 1] in setOf('#', '~') }
                    ) {
                        field[x][y] = '~'
                        if (y > 0)
                            queue.push(x to y - 1)
                        if (x - 1 >= 0 && field[x - 1][y] == '|') {
                            queue.push(x - 1 to y)
                        }
                        if (x + 1 < field.size && field[x + 1][y] == '|') {
                            queue.push(x + 1 to y)
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val lines = generateSequence(::readLine).toList()
    val segments = lines.mapNotNull {
        Regex("""(?<pos>.)=(?<posVal>\d+), .=(?<rangeFrom>\d+)..(?<rangeTo>\d+)""").matchEntire(it)?.groups
    }.map { Segment(it.getInt("posVal"), it.getInt("rangeFrom")..it.getInt("rangeTo"), it["pos"]!!.value == "x") }
    minX = segments.map { if (it.isVertical) it.pos else it.range.first }.minOrNull()!!
    maxX = segments.map { if (it.isVertical) it.pos else it.range.last }.maxOrNull()!!
    minY = segments.map { if (!it.isVertical) it.pos else it.range.first }.minOrNull()!!
    maxY = segments.map { if (!it.isVertical) it.pos else it.range.last }.maxOrNull()!!
    val field = MutableList(1000) { MutableList(maxY - minY + 1) { '.' } }
    field[500][0] = '|'
    for (segment in segments) {
        var (x, y) = if (segment.isVertical) segment.pos to segment.range.first else segment.range.first to segment.pos
        y -= minY
        for (i in 1..segment.range.count()) {
            field[x][y] = '#'
            if (segment.isVertical) ++y else ++x
        }
    }
    simulate(field, 500, 0)
    println(field.sumBy { it.count { it in setOf('~', '|') } })
    println(field.sumBy { it.count { it == '~' } })
}
