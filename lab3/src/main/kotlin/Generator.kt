import java.io.File
import java.io.PrintWriter

class Generator internal constructor(private val grammar: Grammar, private val name: String, private val header: String, private val members: String) {
    private val directory = "src/generated"

    internal fun generateAll() {
        generateToken()
        generateLexer()
        generateParser()
        generateMain()
    }

    internal fun generateToken() {
        val TokenName = name + "Token"
        val file = File(directory, "$TokenName.java")
        PrintWriter(file).use { out ->
            out.println("public enum $TokenName {")
            // printing all the terminals' names as enum members
            val sb = StringBuilder()
            for (term in grammar.terminals.keys) {
                sb.append("\t").append(term.toUpperCase()).append(", \n")
            }
            // an easy way to remove last comma
            if (sb.length >= 3)
                out.println(sb.delete(sb.length - 3, sb.length).toString())
            out.println("}")
        }
    }

    internal fun generateLexer() {
        val LexerName = name + "Lexer"
        val file = File(directory, "$LexerName.java")
        PrintWriter(file).use { out ->
            // imports
            out.println("import java.io.IOException;")
            out.println("import java.io.InputStream;")
            out.println("import java.text.ParseException;\n")

            out.println("class $LexerName {")
            // members
            out.println("\tprivate InputStream input;")
            out.println("\tprivate int curChar, curPos;")
            out.println("\tprivate String curString;")
            out.println("\tprivate " + name + "Token curToken;\n")
            // ctor
            out.println("\t$LexerName(InputStream input) throws ParseException {")
            out.println("\t\tthis.input = input;")
            out.println("\t\tcurPos = 0;")
            out.println("\t\tnextChar();")
            out.println("\t}\n")
            // isBlank func
            // if your grammar allows to redefine blank characters, change it
            out.println("\tprivate boolean isBlank(int c) {\n\t\treturn Character.isWhitespace(c);\n\t}\n")
            // nextChar func
            out.println("\tprivate void nextChar() throws ParseException {")
            out.println("\t\tcurPos++;")
            out.println("\t\ttry {")
            out.println("\t\t\tcurChar = input.read();")
            out.println("\t\t} catch (IOException e) {")
            out.println("\t\t\tthrow new ParseException(e.getMessage(), curPos);")
            out.println("\t\t}")
            out.println("\t}\n")
            // nextToken func
            out.println("\tvoid nextToken() throws ParseException {")
            // skip blanks
            out.println("\t\twhile (isBlank(curChar)) {\n\t\t\tnextChar();\n\t\t}")
            // if eof happens, return END token
            out.println("\t\tif (curChar == -1) {\n\t\t\tcurToken = " + name + "Token.END;\n\t\t\treturn;\n\t\t}\n")
            out.println("\t\tcurString = \"\";")
            out.println("\t\tcurToken = " + name + "Token.END;")
            out.println("\t\t" + name + "Token prev = " + name + "Token.END;")
            // greedily reading forward to be able to match string tokens
            out.println("\t\twhile (curToken == " + name + "Token.END) {")
            out.println("\t\t\tcurString = curString.concat(Character.toString((char)curChar));")
            out.println("\t\t\tswitch (curString) {")
            // printing all terminals as switch cases
            // if one matches, read next char and write the corresponding token to curToken
            for ((_, curr_t) in grammar.terminals) {
                for (str in curr_t.strings) {
                    out.println("\t\t\t\tcase \"$str\":")
                    out.println("\t\t\t\t\tnextChar();")
                    out.println("\t\t\t\t\tcurToken = " + name + "Token." + curr_t.name.toUpperCase() + ";")
                    out.println("\t\t\t\t\tbreak;")
                }
            }
            out.println("\t\t\t\tdefault:")
            out.print("\t\t\t\t\t")
            // here we print all the regexp representations of terminals
            for ((_, curr_t) in grammar.terminals) {
                for (regex in curr_t.regex) {
                    out.println("if (curString.matches(\"" + regex.substring(1, regex.length - 1) + "\")) {")
                    out.println("\t\t\t\t\t\tnextChar();")
                    out.println("\t\t\t\t\t\tcurToken = " + name + "Token." + curr_t.name.toUpperCase() + ";")
                    out.print("\t\t\t\t\t} else ")
                }
            }
            // we went all the way to the eof and could not determine char sequence
            out.println("if ((curChar == -1 || isBlank(curChar)) && prev == " + name + "Token.END) {")
            out.println("\t\t\t\t\t\tthrow new ParseException(\"Illegal character '\" + curString.charAt(0) + \"' at position \", curPos - curString.length());")
            out.println("\t\t\t\t\t}")
            out.println("\t\t\t}")
            // confusing schemes to check if we need to continue reading
            out.println("\t\t\tif (curToken == " + name + "Token.END) {")
            out.println("\t\t\t\tif (prev != " + name + "Token.END) {")
            out.println("\t\t\t\t\tcurString = curString.substring(0, curString.length() - 1);")
            out.println("\t\t\t\t\tcurToken = prev;")
            out.println("\t\t\t\t} else {")
            out.println("\t\t\t\t\tnextChar();")
            out.println("\t\t\t\t}")

            out.println("\t\t\t} else {")
            out.println("\t\t\t\tprev = curToken;")
            out.println("\t\t\t\tcurToken = " + name + "Token.END;")
            out.println("\t\t\t}")
            out.println("\t\t}")
            out.println("\t}")

            // some useless getters
            out.println("\n\t" + name + "Token getCurToken() {")
            out.println("\t\treturn curToken;")
            out.println("\t}")

            out.println("\n\tint getCurPos() {")
            out.println("\t\treturn curPos;")
            out.println("\t}")

            out.println("\n\tString getCurString() {")
            out.println("\t\treturn curString;")
            out.println("\t}")

            out.println("}")
        }
    }

    internal fun generateParser() {
        val ParserName = name + "Parser"
        val LexerName = name + "Lexer"
        val file = File(directory, "$ParserName.java")
        PrintWriter(file).use { out ->
            // user-defined imports
            out.println(header)
            // imports
            out.println("import java.io.InputStream;")
            out.println("import java.text.ParseException;\n")
            // class header
            out.println("public class $ParserName {")
            // user-defined members
            out.println(members)
            // members
            out.println("\tprivate $LexerName lex;\n")
            // main func, return type same as starting nonterm
            out.println("\tpublic " + grammar.start.returnType() + " parse(InputStream input) throws ParseException {")
            out.println("\t\tlex = new $LexerName(input);")
            out.println("\t\tlex.nextToken();")
            out.print("\t\t")
            if (grammar.start.returnType() != "void") out.print("return ")
            out.println(grammar.start.name + "();")
            out.println("\t}\n")

            // generate a function for each nonterm
            val table = grammar.table
            for ((key, curr_nt) in grammar.nonTerminals) {
                // return type & name for a nonterm
                out.print("\tprivate " + curr_nt.returnType() + " " + key + "(")
                // inherited attrs as func args
                val inh = StringBuilder()
                for (attribute in curr_nt.arguments) {
                    inh.append(attribute.type).append(" ").append(attribute.name).append(", ")
                }
                // again removing last comma
                if (inh.length >= 2)
                    out.print(inh.delete(inh.length - 2, inh.length).toString())
                out.println(") throws ParseException {")

                // user-defined init
                out.println(curr_nt.init)
                // declare ret val
                if (curr_nt.returnType() != "void")
                    out.println("\t\t" + curr_nt.returnType() + " " + curr_nt.returnAttribute.name + " = " + defaultValue(curr_nt.returnType()) + ";")

                // here start the rules
                out.println("\t\tswitch (lex.getCurToken()) {")
                for ((key1, rule) in table[curr_nt]!!) {
                    // a case for each possible term
                    out.println("\t\t\tcase " + key1.name.toUpperCase() + ": {")
                    // check whether it was not inferred through EPS rule
                    val own = rule.head().name != grammar.EPS.name
                    for (i in rule.units.indices) {
                        val elem = rule.units[i].element
                        if (elem is Terminal) {
                            out.println(rule.units[i].code)
                            if (own) out.println("\t\t\t\tlex.nextToken();")
                        } else {
                            // here we are going to call nonterms from rule
                            if (elem.returnType() != "void") {
                                out.print("\t\t\t\t" + elem.returnType() + " " + elem.name + " = " + elem.name + "(")
                                val args = StringBuilder()
                                for (arg in rule.units[i].args) {
                                    args.append(arg).append(", ")
                                }
                                if (args.length >= 2)
                                    out.print(args.delete(args.length - 2, args.length).toString())
                                out.println(");")
                            } else {
                                out.println("\t\t\t\t" + elem.name + "();")
                            }
                            out.println("\t\t\t\t" + rule.units[i].code)
                        }
                    }
                    out.println("\t\t\t\tbreak;\n\t\t\t}")
                }

                out.print(
                        "\t\t\tdefault:\n" +
                                "\t\t\t\tthrow new AssertionError();\n" +
                                "\t\t}\n"
                )
                if (curr_nt.returnType() != "void") {
                    out.println("\t\treturn " + curr_nt.returnAttribute.name + ";")
                }
                out.println("\t}")
            }

            out.print("}\n")
        }
    }

    internal fun generateMain() {
        val file = File(directory, "Main.java")
        PrintWriter(file).use { out ->
            out.println("import java.io.*;")
            out.println("import java.text.ParseException;")
            out.println("import java.nio.charset.StandardCharsets;")

            out.println("\npublic class Main {")
            out.println("\tpublic static void main(String[] args) {")
            out.println("\t\t" + name + "Parser parser = new " + name + "Parser();")
            if (grammar.start.returnType() != "void") {
                out.println("\t\t" + grammar.start.returnType() + " res;")
            }
            out.println("\t\ttry {")
            out.println("\t\t\tif (args.length != 0) {")
            out.println("\t\t\t\ttry {")
            out.println("\t\t\t\t\tres = parser.parse(new ByteArrayInputStream(args[0].getBytes(StandardCharsets.UTF_8.name())));")
            out.println("\t\t\t\t} catch (UnsupportedEncodingException e) {")
            out.println("\t\t\t\t\tSystem.err.println(e.getMessage());")
            out.println("\t\t\t\t\tSystem.exit(1);")
            out.println("\t\t\t\t}")
            out.println("\t\t\t} else {")
            out.println("\t\t\t\ttry {")
            out.println("\t\t\t\t\tInputStream input = new FileInputStream(new File(\"input.txt\"));")
            out.println("\t\t\t\t\tres = parser.parse(input);")
            out.println("\t\t\t\t} catch (FileNotFoundException e) {")
            out.println("\t\t\t\t\tSystem.err.println(e.getMessage());")
            out.println("\t\t\t\t\tSystem.exit(1);")
            out.println("\t\t\t\t}")
            out.println("\t\t\t}")
            out.println("\t\t} catch (ParseException e) {")
            out.println("\t\t\tSystem.err.println(\"Parser failed: \\nCause: \" + e.getMessage() + e.getErrorOffset());")
            out.println("\t\t\tSystem.exit(1);")
            out.println("\t\t}")
            out.println("\t}")
            out.print("}")
        }
    }

    private fun defaultValue(name: String): String {
        return if (Character.isUpperCase(name[0]))
            "null"
        else {
            if (name == "boolean")
                "false"
            else
                "0"
        }
    }
}
