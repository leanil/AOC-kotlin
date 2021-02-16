// https://adventofcode.com/2020/day/7
data class Edge(val to: String, val weight: Int)

typealias Graph = Map<String, List<Edge>>

operator fun Graph.plus(other: Graph): Graph =
    (asSequence() + other.asSequence()).groupBy { it.key }
        .map { it.key to it.value.fold(emptyList<Edge>(), { acc, entry -> acc + entry.value }) }.toMap()

fun countBagsInside(graph: Graph, node: String): Int =
    graph.getValue(node).sumBy { it.weight * (1 + countBagsInside(graph, it.to)) }

fun countBagsOutside(graph: Graph, node: String): Int {
    fun findParents(node: String) = graph.filter { it.value.any {it.to == node} }.keys
    val parents = mutableSetOf<String>()
    fun addAncestors(node: String) {
        for (parent in findParents(node)) {
            if (parent !in parents) {
                parents.add(parent)
                addAncestors(parent)
            }
        }
    }
    addAncestors(node)
    return parents.count()
}

fun main() {
    val lines = generateSequence(::readLine)
    val graph = lines.fold(
        emptyMap<String, List<Edge>>(), { graph, line ->
            val groups =
                Regex("""(?<sourceBag>\w+ \w+) \w+ \w+( \w+ \w+ \w+.|(?<more>.*))""").matchEntire(line)?.groups
            if (groups == null)
                graph
            else {
                val sourceBag = groups["sourceBag"]!!.value
                graph + mapOf(
                    sourceBag to Regex(""" (?<weight>\d+) (?<targetBag>\w+ \w+) \w+.""").findAll(
                        groups["more"]?.value ?: ""
                    ).map {
                        Edge(
                            it.groups["targetBag"]!!.value,
                            it.groups["weight"]!!.value.toInt()
                        )
                    }.toList()
                )
            }
        })

    println(countBagsOutside(graph, "shiny gold"))
    println(countBagsInside(graph, "shiny gold"))
}
