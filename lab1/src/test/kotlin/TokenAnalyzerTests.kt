import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.InputStream

class TokenAnalyzerTests {
    private lateinit var tokenAnalyzer: TokenAnalyzer

    private fun getInputStream(string: String): InputStream {
        return string.byteInputStream()
    }

    @ParameterizedTest
    @ValueSource(ints = [ '|'.toInt(), '('.toInt(), ')'.toInt(), '*'.toInt(), -1 ])
    internal fun testOrParsing(char: Int) {
        tokenAnalyzer = TokenAnalyzer(char.toChar().toString().byteInputStream())
        val parsedToken = tokenAnalyzer.nextToken()
        val expectedToken = Token.from(char)
        assert(expectedToken == parsedToken)
        if (char != -1) {
            assert(expectedToken.char == char.toChar())
        }
    }

}