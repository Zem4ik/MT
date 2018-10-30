import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Grammar(val terminals: MutableMap<String, Terminal>, val nonTerminals: MutableMap<String, NonTerminal>, val start: NonTerminal) {
    val EPS = Terminal("EPS")
    val END = Terminal("END")


    var first = HashMap<String, MutableSet<String>>()
    var follow = HashMap<String, MutableSet<String>>()

    val table: Map<NonTerminal, Map<Terminal, Rule>>
        get() {
            val table = HashMap<NonTerminal, Map<Terminal, Rule>>()
            for (nonTerminal in nonTerminals.values) {
                val row = HashMap<Terminal, Rule>()
                for (terminal in terminals.values) {
                    for (rule in nonTerminal.rules) {
                        if (first[rule.head().name]!!.contains(terminal.name)
                                || first[rule.head().name]!!.contains(EPS.name) && follow[nonTerminal.name]!!.contains(terminal.name)) {
                            row[terminal] = rule
                        }
                    }
                }
                table[nonTerminal] = row
            }
            return table
        }

    init {
        EPS.addString(EPS.name)
        END.addString(END.name)

        this.terminals[EPS.name] = EPS
        this.terminals[END.name] = END
        countFirst()
        countFollow()
    }

    private fun countFirst() {
        for (name in terminals.keys) {
            val set = HashSet<String>()
            set.add(name)
            first[name] = set
        }
        for (nonTerminal in nonTerminals.keys) {
            first[nonTerminal] = HashSet()
        }

        var changed: Boolean
        do {
            changed = false
            for (value in nonTerminals.values) {
                for (rule in value.rules) {
                    for (i in 0 until rule.units.size) {
                        val element = rule.units[i].element
                        val toAdd = HashSet(first[element.name])
                        val shouldContinue = toAdd.remove(EPS.name)
                        changed = changed or first[value.name]!!.addAll(toAdd)
                        if (shouldContinue) {
                            if (i + 1 == rule.units.size) {
                                first[value.name]!!.add(EPS.name)
                            }
                        } else {
                            break
                        }
                    }

                }
            }
        } while (changed)
    }

    private fun countFollow() {
        for (nonTerm in nonTerminals.keys) {
            follow[nonTerm] = HashSet()
        }

        follow[start.name]!!.add(END.name)
        var changed: Boolean
        do {
            changed = false
            for (nonTerminal in nonTerminals.values) {
                for (rule in nonTerminal.rules) {
                    for (i in rule.units.indices.reversed()) {
                        val element = rule.units[i].element
                        if (element is NonTerminal) {
                            if (i + 1 == rule.units.size) {
                                changed = changed or follow[element.name]!!.addAll(follow[nonTerminal.name]!!)
                            }
                            for (j in i + 1 until rule.units.size) {
                                val nextElement = rule.units[j].element
                                val toAdd = HashSet(first[nextElement.name])
                                val shouldContinue = toAdd.remove(EPS.name)
                                changed = changed or follow[element.name]!!.addAll(toAdd)
                                if (shouldContinue) {
                                    if (j + 1 == rule.units.size) {
                                        follow[element.name]!!.addAll(follow[nonTerminal.name]!!)
                                    }
                                } else {
                                    break
                                }
                            }
                        }
                    }
                }
            }
        } while (changed)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Start: ").append(start.name).append('\n')
        sb.append("\nNonTerms:\n")
        nonTerminals.forEach { name, elem ->
            sb.append(name).append('\n')
            if (!elem.rules.isEmpty()) {
                sb.append("\t: ").append(elem.rules[0]).append('\n')
            }
            for (i in 1 until elem.rules.size) {
                sb.append("\t| ").append(elem.rules[i]).append('\n')
            }
            sb.append(";\n")
        }
        sb.append("\nTerms:\n")
        terminals.forEach { name, elem ->
            sb.append(name).append('\n')
            if (!elem.strings.isEmpty()) {
                sb.append("\t: ").append(elem.strings[0])
            }
            for (i in 1 until elem.strings.size) {
                sb.append(" | ").append(elem.strings[i])
            }
            if (!elem.regex.isEmpty()) {
                if (elem.strings.isEmpty()) {
                    sb.append("\t: ").append(elem.regex[0])
                } else {
                    sb.append("\n\t| ").append(elem.regex[0])
                }
            }
            for (i in 1 until elem.regex.size) {
                sb.append(" | ").append(elem.regex[i])
            }
            sb.append(";\n")
        }
        sb.append("\nFirst:\n")
        first.forEach { k, v ->
            if (nonTerminals.containsKey(k)) {
                sb.append(k).append(" : ").append(v).append('\n')
            }
        }
        sb.append("\nFollow:\n")
        follow.forEach { k, v -> sb.append(k).append(" : ").append(v).append('\n') }
        return sb.toString()
    }
}
