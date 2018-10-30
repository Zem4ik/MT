import java.io.File
import java.io.PrintWriter

private val NAME: String = "test"
private val DIR = "src/main/java"

private var first = HashMap<String, Set<String>>()
private var follow = HashMap<String, Set<String>>()

internal fun generate() {
    generateToken()
}

internal fun generateToken() {
    val TokenName = NAME + "Token"
    val file = File(DIR, "$TokenName.java")
    PrintWriter(file).use { out ->
        out.println("public enum $TokenName {")
        // printing all the terminal's names as enum members
        val sb = StringBuilder()
        for (term in terminals.keys) {
            sb.append("\t").append(term.toUpperCase()).append(", \n")
        }
        // an easy way to remove last comma
        if (sb.length >= 3)
            out.println(sb.delete(sb.length - 3, sb.length).toString())
        out.println("}")
    }
}

