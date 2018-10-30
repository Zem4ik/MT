import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class Lexer {
	private InputStream input;
	private int curChar, curPos;
	private String curString;
	private Token curToken;

	Lexer(InputStream input) throws ParseException {
		this.input = input;
		curPos = 0;
		nextChar();
	}

	private boolean isBlank(int c) {
		return Character.isWhitespace(c);
	}

	private void nextChar() throws ParseException {
		curPos++;
		try {
			curChar = input.read();
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), curPos);
		}
	}

	void nextToken() throws ParseException {
		while (isBlank(curChar)) {
			nextChar();
		}
		if (curChar == -1) {
			curToken = Token.END;
			return;
		}

		curString = "";
		curToken = Token.END;
		Token prev = Token.END;
		while (curToken == Token.END) {
			curString = curString.concat(Character.toString((char)curChar));
			switch (curString) {
				case "EPS":
					nextChar();
					curToken = Token.EPS;
					break;
				case "END":
					nextChar();
					curToken = Token.END;
					break;
				default:
					if ((curChar == -1 || isBlank(curChar)) && prev == Token.END) {
						throw new ParseException("Illegal character '" + curString.charAt(0) + "' at position ", curPos - curString.length());
					}
			}
			if (curToken == Token.END) {
				if (prev != Token.END) {
					curString = curString.substring(0, curString.length() - 1);
					curToken = prev;
				} else {
					nextChar();
				}
			} else {
				prev = curToken;
				curToken = Token.END;
			}
		}
	}

	Token getCurToken() {
		return curToken;
	}

	int getCurPos() {
		return curPos;
	}

	String getCurString() {
		return curString;
	}
}
