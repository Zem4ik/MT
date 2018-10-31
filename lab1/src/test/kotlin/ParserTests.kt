import org.junit.jupiter.api.Test

class ParserTests {

    private val parser = Parser()

    @Test
    internal fun testE1() {
        var node = parser.parse("".byteInputStream())
        assert(node.nodeType == NodeType.E1 && node.letter == epsilon)

        node = parser.parse("a|b".byteInputStream())
        assert(node.nodeType == NodeType.E1
                && node.children.size == 2
                && node.children[0].nodeType == NodeType.C1
                && node.children[1].nodeType == NodeType.E2)
    }

    @Test
    internal fun testE2() {
        var node = parser.parse("a".byteInputStream())
        // take E1 from rule E1 -> C1 E2
        node = node.children[1]
        assert(node.nodeType == NodeType.E2 && node.letter == epsilon)

        node = parser.parse("a|b".byteInputStream())
        // take E1 from rule E1 -> C1 E2
        node = node.children[1]
        assert(node.nodeType == NodeType.E2
                && node.children.size == 3
                && node.children[0].nodeType == NodeType.Terminal
                && node.children[1].nodeType == NodeType.C1
                && node.children[2].nodeType == NodeType.E2
                && node.children[2].letter == epsilon)

        node = parser.parse("a|b|cd".byteInputStream())
        // take E1 from rule E1 -> C1 E2
        node = node.children[1]
        assert(node.nodeType == NodeType.E2
                && node.children.size == 3
                && node.children[0].nodeType == NodeType.Terminal
                && node.children[1].nodeType == NodeType.C1
                && node.children[2].nodeType == NodeType.E2
                && node.children[2].children.size != 0)
    }

    @Test
    internal fun testC1() {
        var node = parser.parse("a".byteInputStream())
        // take E1 from rule C1 -> C1 E2
        // take S from rule C1 -> S K C2
        node = node.children[0].children[0]
        assert(node.nodeType == NodeType.S
                && node.letter == 'a')
    }
}