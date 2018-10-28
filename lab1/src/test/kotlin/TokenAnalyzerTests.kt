import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.InputStream

class TokenAnalyzerTests {
    private lateinit var tokenAnalyzer: TokenAnalyzer

    private fun getInputStream(string: String): InputStream {
        return string.byteInputStream()
    }

    @Test
    internal fun testLettersParsing() {
        for (char in 'a'..'z') {
            testTokensParsing(char.toInt())
        }
    }

    @ParameterizedTest
    @ValueSource(ints = ['|'.toInt(), '('.toInt(), ')'.toInt(), '*'.toInt(), -1])
    internal fun testTokensParsing(char: Int) {
        val inputStream: InputStream = object : InputStream() {
            override fun read(): Int {
                return char
            }

        }
        tokenAnalyzer = TokenAnalyzer(inputStream)
        val parsedToken = tokenAnalyzer.nextToken()
        val expectedToken = Token.from(char)
        assert(expectedToken == parsedToken) {
            "expected: ${expectedToken}, but got ${parsedToken}"
        }
        if (char != -1) {
            assert(expectedToken.char == char.toChar())
        }
    }

}