import grammar.GrammarParser

val EPS : LexerRule = LexerRule("EPS")
val END : LexerRule = LexerRule("END")

class LexerRule {
    val name : String
    val alternatives : List<GrammarParser.LexerAltContext>

    constructor(name : String) {
        this.name = name
        alternatives = ArrayList()
    }

    constructor(rule : GrammarParser.LexerRuleSpecContext) {
        name = rule.LexerName().text
        alternatives = rule.lexerRuleBlock().lexerAltList().lexerAlt()
    }
}