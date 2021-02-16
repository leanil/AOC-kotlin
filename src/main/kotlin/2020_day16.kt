// https://adventofcode.com/2020/day/16
data class TicketField(val name: String, val min1: Int, val max1: Int, val min2: Int, val max2: Int) {
    fun matches(x: Int) = x in min1..max1 || x in min2..max2
}

fun main() {
    val lines = generateSequence(::readLine).toList()
    val rulesEnd = lines.indexOf("")
    val fields = lines.take(rulesEnd).mapNotNull {
        val pattern = """(?<name>.+): (?<min1>\d+)-(?<max1>\d+) or (?<min2>\d+)-(?<max2>\d+)"""
        Regex(pattern).matchEntire(it)?.groups
    }.map {
        TicketField(
            it["name"]!!.value, it.getInt("min1"), it.getInt("max1"),
            it.getInt("min2"), it.getInt("max2")
        )
    }.toSet()

    fun String.toTicket() = if (contains(',')) split(',').map { it.toInt() }.toIntArray() else null

    val myTicket = lines[rulesEnd + 2].toTicket()!!
    val nearbyTickets = lines.drop(rulesEnd + 5).mapNotNull { it.toTicket() }
    val errorRate = nearbyTickets.flatMap { it.toList() }.filter { number -> fields.none { it.matches(number) } }.sum()
    val validTickets = nearbyTickets.filter { it.all { number -> fields.any { it.matches(number) } } }

    val possibleFields = Array(fields.size) { i ->
        fields.filter { field -> validTickets.all { field.matches(it[i]) } }.toMutableSet()
    }
    val isFixed = Array(fields.size) { false }
    for (i in 1..fields.size) {
        val nextFix = possibleFields.withIndex().find { it.value.size == 1 && !isFixed[it.index] }!!
        isFixed[nextFix.index] = true
        possibleFields.forEachIndexed { index, set ->
            if (index != nextFix.index) {
                set.remove(nextFix.value.first())
            }
        }
    }
    val departureProduct =
        myTicket.filterIndexed { index, _ -> possibleFields[index].first().name.startsWith("departure") }
            .fold(1L, Long::times)

    println(errorRate)
    println(departureProduct)
}
