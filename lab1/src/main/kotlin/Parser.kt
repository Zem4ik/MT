import java.io.InputStream

public enum class NodeType {
    E1, E2, C1, C2, S, K, Terminal
}

public class Node(val nodeType: NodeType, vararg children: Node) {
    val children: ArrayList<Node> = ArrayList()

    init {
        for (node in children) {
            if (!node.isSimple()) this.children.add(node)
        }
    }

    var letter: Char = '\u0000'

    fun isSimple() : Boolean {
        return (nodeType == NodeType.Terminal && letter == '\u0000' && children.size == 0)
    }

    override fun toString(): String {
        if (nodeType == NodeType.Terminal || nodeType == NodeType.S) {
            return if (letter == '\u0000')
                "T"
            else
                letter.toString()
        }
        return nodeType.toString()
    }
}

public class Parser {
    private lateinit var tokenAnalyzer: TokenAnalyzer
    private lateinit var token: Token

    fun parse(inputStream: InputStream): Node {
        tokenAnalyzer = TokenAnalyzer(inputStream)
        token = tokenAnalyzer.nextToken()
        return parseInternal(NodeType.E1)
    }

    private fun terminalNode(nodeType: NodeType, letter: Char? = null): Node {
        val result = Node(nodeType,
                Node(NodeType.Terminal))
        letter?.let {
            result.letter = it
            token = tokenAnalyzer.nextToken()
        }
        return result
    }

    private fun checkToken(token: Token): Node {
        return if (this.token == token)
            terminalNode(NodeType.Terminal, token.char)
        else
            throw throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
    }

    private fun parseInternal(nodeType: NodeType): Node {
        when (nodeType) {
            NodeType.E1 -> {
                return when (token) {
                    Token.END -> terminalNode(nodeType)
                    else -> Node(nodeType,
                            parseInternal(NodeType.C1),
                            parseInternal(NodeType.E2))
                }
            }
            NodeType.E2 -> {
                return when (token) {
                    Token.END -> terminalNode(nodeType)
                    else -> try {
                        Node(nodeType,
                                checkToken(Token.OR),
                                parseInternal(NodeType.C1),
                                parseInternal(NodeType.E2))
                    } catch (e: ParseException) {
                        terminalNode(nodeType)
                    }
                }
            }
            NodeType.C1 -> {
                return Node(nodeType, parseInternal(NodeType.S), parseInternal(NodeType.K), parseInternal(NodeType.C2))
            }
            NodeType.C2 -> {
                return when (token) {
                    Token.END -> terminalNode(nodeType)
                    else -> try {
                        Node(nodeType,
                                parseInternal(NodeType.S),
                                parseInternal(NodeType.K),
                                parseInternal(NodeType.C2))
                    } catch (e: ParseException) {
                        terminalNode(nodeType)
                    }
                }
            }
            NodeType.S -> {
                return when (token) {
                    Token.LETTER -> terminalNode(nodeType, token.char)
                    Token.LPAREN -> Node(NodeType.E1,
                            checkToken(Token.LPAREN),
                            parseInternal(NodeType.E1),
                            checkToken(Token.RPAREN))
                    else -> throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
                }
            }
            NodeType.K -> {
                return when (token) {
                    Token.STAR -> terminalNode(nodeType, '*')
                    else -> terminalNode(nodeType)
                }
            }
            else -> throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
        }
    }
}

