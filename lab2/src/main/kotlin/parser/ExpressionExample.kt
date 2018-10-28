package parser

import ExpressionLexer
import ExpressionParser
import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream


fun main(args: Array<String>) {
    val input = ANTLRFileStream("input.txt")
    val lexer = ExpressionLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    val tree = parser.expr()

    val calcVisitor = ExpressionVisitor()
    calcVisitor.visit(tree)
}