import java.io.IOException
import java.io.InputStream

internal enum class Token {
    OR,
    LPAREN,
    RPAREN,
    STAR,
    LETTER,
    END;

    var char: Char = '\u0000'

    companion object {
        fun from(symbol: Int): Token {
            val token: Token
            if (symbol.toChar().isLetter()) {
                token = LETTER;
                token.char = symbol.toChar()

            } else {
                token = when (symbol) {
                    '|'.toInt() -> Token.OR
                    '('.toInt() -> Token.LPAREN
                    ')'.toInt() -> Token.RPAREN
                    '*'.toInt() -> Token.STAR
                    -1          -> Token.END
                    else        -> throw ParseException("Unexpected token: ${symbol}")
                }
                token.char = symbol.toChar()
            }
            return token
        }
    }

}

internal class ParseException : IllegalArgumentException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}

internal class TokenAnalyzer(val inputStream: InputStream) {

    public var currentPosition: Int = 0
        private set

    private fun Char.isBank() : Boolean {
        return this == ' ' || this == '\r' || this == '\n' || this == '\t';
    }

    /**
     * @exception IOException
     */
    private fun InputStream.nextChar(): Int {
        currentPosition++;
        try {
            return read()
        } catch (e : IOException) {
            currentPosition = 0
            close()
            throw e
        }
    }

    /**
     * @exception ParseException if parsing error occurs
     * @exception IOException if an I/O error occurs
     */
    internal fun nextToken(): Token {
        var nextChar = inputStream.nextChar()
        while (nextChar.toChar().isBank()) {
            nextChar = inputStream.nextChar()
        }
        return Token.from(nextChar)
    }

}