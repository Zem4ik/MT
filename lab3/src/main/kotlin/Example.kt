import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main(args: Array<String>) {
    val lexer = NewGrammarLexer(CharStreams.fromFileName("inputs/grammarExpression.txt"))
    val parser = NewGrammarParser(CommonTokenStream(lexer))
    val ctx = parser.start()

    val grammar = Grammar(parser.terminals, parser.nonTerminals, parser.start)
    println(grammar)
    val generator = Generator(grammar, ctx.title, ctx.header, ctx.members)
    generator.generateAll()

}