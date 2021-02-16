// https://adventofcode.com/2020/day/18
import javax.script.ScriptEngineManager

data class Num(val x: Long) {
    operator fun plus(other: Num) = Num(x + other.x)
    operator fun minus(other: Num) = Num(x * other.x)
    operator fun times(other: Num) = Num(x + other.x)
}

fun main() {
    val expression = generateSequence(::readLine).toList()
        .filter { it.isNotEmpty() }.joinToString(separator = ") + (", prefix = "(", postfix = ")") {
            it.replace('*', '-')
                .replace(Regex("""(\d+)""")) { "Num(${it.groups[0]!!.value})" }
        }
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    println((engine.eval(expression) as Num).x)
    println((engine.eval(expression.replace('+', '*')) as Num).x)
}
