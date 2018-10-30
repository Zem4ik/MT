import com.squareup.javapoet.*
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.nio.file.Path
import javax.lang.model.element.Modifier

private val ExceptionType = IllegalArgumentException::class.java
private const val PACKAGE_NAME = "generated.grammar"

enum class Action {
    HEADER,
    MEMBERS;

    companion object {
        fun from(string: String) : Action {
            return when(string) {
                "header"    -> HEADER
                "members"   -> MEMBERS
                else        -> throw IllegalArgumentException("Wrong action name: $string")
            }
        }
    }

}

internal lateinit var name : String
internal val actions = HashMap<Action, String>()
internal val nonTerminals = HashMap<String, ParserRule>()
internal val terminals = HashMap<String, LexerRule>()

public fun parse(fileName : String) {
    parse(CharStreams.fromFileName(fileName))
}

public fun parse(path: Path) {
    parse(CharStreams.fromPath(path))
}

public fun parse(charStream: CharStream) {
    val lexer = GrammarLexer(charStream)
    val parser = GrammarParser(CommonTokenStream(lexer))
    val grammarSpecContext = parser.grammarSpec()

    // grammar name
    name = grammarSpecContext.grammarName().LexerName().text
    // parsing actions
    for (action in grammarSpecContext.action()) {
        val key = Action.from(action.actionId().text);
        //todo
        if (actions.containsKey(key)) throw IllegalArgumentException()
        actions[key] = action.ActionBlock().text
    }
    // parsing rules
    for (rule in grammarSpecContext.ruleSpec()) {
        rule.lexerRuleSpec()?.let {
            terminals[it.LexerName().text] = LexerRule(it) //it.lexerRuleBlock().text
        }
        rule.parserRuleSpec()?.let {
            nonTerminals[it.ParserName().text] = ParserRule(it)
        }
    }
    terminals[EPS.name] = EPS
    terminals[END.name] = END

    generate()
//    val dir = Paths.get("src/main/java")
//    generateLexerTokens(dir)
//    generateLexer(dir)

}

fun generateLexerTokens(dir: Path) {
    val tokens = TypeSpec.enumBuilder("Token").addModifiers(Modifier.PUBLIC)
    for (key in terminals.keys) {
        tokens.addEnumConstant(key)
    }

    JavaFile.builder(PACKAGE_NAME, tokens.build()).build().writeTo(dir)
}

fun generateLexer(dir: Path) {
    val INPUT = "input"
    val CUR_CHAR = "curChar"
    val NEXT_CHAR = "nextChar()"
    val CUR_POS = "curPos"
    val CUR_STRING = "curString"
    val CUR_TOKEN = "curToken"

    var lexer = TypeSpec.classBuilder("Lexer")
            .addField(InputStream::class.java, INPUT, Modifier.PRIVATE)
            .addField(TypeName.INT, CUR_CHAR, Modifier.PRIVATE)
            .addField(TypeName.INT, CUR_POS, Modifier.PRIVATE)
            .addField(String::class.java, CUR_STRING, Modifier.PRIVATE)
            .addField(ClassName.bestGuess("Token"), CUR_TOKEN, Modifier.PRIVATE)

    val constructor = MethodSpec.constructorBuilder()
            .addParameter(InputStream::class.java, INPUT)
            .addException(ExceptionType)
            .addStatement("this.$INPUT = $INPUT")
            .addStatement("$CUR_CHAR = 0")
            .addStatement("$CUR_CHAR = 0")
            .addStatement(NEXT_CHAR)
            .build()

    val isBlank = MethodSpec.methodBuilder("isBlank")
            .returns(TypeName.BOOLEAN)
            .addModifiers(Modifier.PRIVATE)
            .addParameter(TypeName.INT, "c")
            .addStatement("return \$T.isWhitespace(c)", Character::class.java)
            .build()

    val nextChar = MethodSpec.methodBuilder("nextChar")
            .addException(ExceptionType)
            .addCode(CodeBlock.of(
                        "$CUR_POS++;\n" +
                        "try {\n" +
                        "   $CUR_CHAR = input.read();\n" +
                        "} catch (\$T e) {\n" +
                        "   throw new \$T();\n" +
                        "}", IOException::class.java, ExceptionType
            ))
            .build()

    val nextToken = MethodSpec.methodBuilder("nextToken")
            .addException(ExceptionType)
            .addCode(
                    "while (isBlank($CUR_CHAR)) {\n" +
                        "nextChar();\n" +
                    "}\n" +
                    "if (curChar == -1) {\n" +
                    "   $CUR_TOKEN = Token.END;\n" +
                    "   return;\n" +
                    "}\n" +
                    "$CUR_STRING = \"\";\n" +
                    "$CUR_TOKEN = Token.END;\n" +
                    "Token prev = Token.END;\n" +
                    "while ($CUR_TOKEN == Token.END)\n" +
                    "   $CUR_STRING = $CUR_STRING.concat(\$T.toString((char) $CUR_CHAR));\n"
            )

    lexer = lexer.addMethod(constructor)
            .addMethod(isBlank)
            .addMethod(nextChar)

    JavaFile.builder(PACKAGE_NAME, lexer.build()).build().writeTo(dir)

}