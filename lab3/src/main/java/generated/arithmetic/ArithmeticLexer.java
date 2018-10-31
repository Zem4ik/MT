package generated.arithmetic;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

class ArithmeticLexer {
	private InputStream input;
	private int curChar, curPos;
	private String curString;
	private ArithmeticToken curToken;

	ArithmeticLexer(InputStream input) throws ParseException {
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
			curToken = ArithmeticToken.END;
			return;
		}

		curString = "";
		curToken = ArithmeticToken.END;
		ArithmeticToken prev = ArithmeticToken.END;
		while (curToken == ArithmeticToken.END) {
			curString = curString.concat(Character.toString((char)curChar));
			switch (curString) {
				case "+":
					nextChar();
					curToken = ArithmeticToken.ADD;
					break;
				case "(":
					nextChar();
					curToken = ArithmeticToken.TERM0;
					break;
				case "*":
					nextChar();
					curToken = ArithmeticToken.MUL;
					break;
				case ")":
					nextChar();
					curToken = ArithmeticToken.TERM1;
					break;
				case "EPS":
					nextChar();
					curToken = ArithmeticToken.EPS;
					break;
				case "^^":
					nextChar();
					curToken = ArithmeticToken.POW;
					break;
				case "END":
					nextChar();
					curToken = ArithmeticToken.END;
					break;
				default:
					if (curString.matches("[1-9][0-9]*|0")) {
						nextChar();
						curToken = ArithmeticToken.NUM;
					} else if ((curChar == -1 || isBlank(curChar)) && prev == ArithmeticToken.END) {
						throw new ParseException("Illegal character '" + curString.charAt(0) + "' at position ", curPos - curString.length());
					}
			}
			if (curToken == ArithmeticToken.END) {
				if (prev != ArithmeticToken.END) {
					curString = curString.substring(0, curString.length() - 1);
					curToken = prev;
				} else {
					nextChar();
				}
			} else {
				prev = curToken;
				curToken = ArithmeticToken.END;
			}
		}
	}

	ArithmeticToken getCurToken() {
		return curToken;
	}

	int getCurPos() {
		return curPos;
	}

	String getCurString() {
		return curString;
	}
}
