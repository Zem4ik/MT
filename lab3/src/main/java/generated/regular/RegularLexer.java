package generated.regular;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class RegularLexer {
	private InputStream input;
	private int curChar, curPos;
	private String curString;
	private RegularToken curToken;

	RegularLexer(InputStream input) throws ParseException {
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
			curToken = RegularToken.END;
			return;
		}

		curString = "";
		curToken = RegularToken.END;
		RegularToken prev = RegularToken.END;
		while (curToken == RegularToken.END) {
			curString = curString.concat(Character.toString((char)curChar));
			switch (curString) {
				case "|":
					nextChar();
					curToken = RegularToken.TERM0;
					break;
				case "(":
					nextChar();
					curToken = RegularToken.TERM2;
					break;
				case ")":
					nextChar();
					curToken = RegularToken.TERM3;
					break;
				case "*":
					nextChar();
					curToken = RegularToken.TERM4;
					break;
				case "EPS":
					nextChar();
					curToken = RegularToken.EPS;
					break;
				case "END":
					nextChar();
					curToken = RegularToken.END;
					break;
				default:
					if (curString.matches("[a-zA-Z]")) {
						nextChar();
						curToken = RegularToken.TERM1;
					} else if ((curChar == -1 || isBlank(curChar)) && prev == RegularToken.END) {
						throw new ParseException("Illegal character '" + curString.charAt(0) + "' at position ", curPos - curString.length());
					}
			}
			if (curToken == RegularToken.END) {
				if (prev != RegularToken.END) {
					curString = curString.substring(0, curString.length() - 1);
					curToken = prev;
				} else {
					nextChar();
				}
			} else {
				prev = curToken;
				curToken = RegularToken.END;
			}
		}
	}

	RegularToken getCurToken() {
		return curToken;
	}

	int getCurPos() {
		return curPos;
	}

	String getCurString() {
		return curString;
	}
}
