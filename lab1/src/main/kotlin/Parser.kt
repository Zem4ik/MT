import java.io.InputStream

internal val epsilon = '\u03B5';

public enum class NodeType {
    E1, E2, C1, C2, S, K, Terminal;

    override fun toString(): String {
        if (this == Terminal) {
            return "T"
        }
        return super.toString()
    }
}

public class Node(val nodeType: NodeType, vararg children: Node) {
    val children: ArrayList<Node> = ArrayList()

    init {
        for (node in children) {
            if (!node.isSimple()) this.children.add(node)
        }
    }

    var letter: Char = '\u0000'

    fun isSimple(): Boolean {
        return (nodeType == NodeType.Terminal && letter == '\u0000' && children.size == 0)
    }

    override fun toString(): String {
        if (letter != '\u0000') {
            return "${nodeType}: ${letter}"
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

    private fun terminalNode(letter: Char? = null, nodeType: NodeType = NodeType.Terminal): Node {
        val result = Node(nodeType)
        letter?.let {
            result.letter = it
            if (letter != epsilon) {
                token = tokenAnalyzer.nextToken()
            }
        }
        return result
    }

    private fun checkToken(token: Token): Node {
        return if (this.token == token)
            terminalNode(token.char, NodeType.Terminal)
        else
            throw throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
    }

    private fun parseInternal(nodeType: NodeType): Node {
        when (nodeType) {
            NodeType.E1 -> {
                return when (token) {
                    Token.LETTER,
                    Token.LPAREN -> Node(nodeType,
                            parseInternal(NodeType.C1),
                            parseInternal(NodeType.E2))
                    Token.END -> terminalNode(epsilon, nodeType)
                    else -> throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
                }
            }
            NodeType.E2 -> {
                return when (token) {
                    Token.OR -> Node(nodeType,
                            terminalNode(token.char),
                            parseInternal(NodeType.C1),
                            parseInternal(NodeType.E2))
                    else -> terminalNode(epsilon, nodeType)
                }
            }
            NodeType.C1 -> {
                return Node(nodeType,
                        parseInternal(NodeType.S),
                        parseInternal(NodeType.K),
                        parseInternal(NodeType.C2))
            }
            NodeType.C2 -> {
                return when (token) {
                    Token.LETTER,
                    Token.LPAREN -> Node(nodeType,
                            parseInternal(NodeType.S),
                            parseInternal(NodeType.K),
                            parseInternal(NodeType.C2))
                    else -> terminalNode(epsilon, nodeType)
                }
            }
            NodeType.S -> {
                return when (token) {
                    Token.LETTER -> terminalNode(token.char, nodeType)
                    Token.LPAREN -> Node(NodeType.S,
                            terminalNode(token.char),
                            parseInternal(NodeType.E1),
                            checkToken(Token.RPAREN))
                    else -> throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
                }
            }
            NodeType.K -> {
                return when (token) {
                    Token.STAR -> Node(nodeType,
                                terminalNode('*', nodeType),
                                parseInternal(NodeType.K))
                    else -> terminalNode(epsilon, nodeType)
                }
            }
            else -> throw ParseException("Expected character at ${tokenAnalyzer.currentPosition}")
        }
    }
}

