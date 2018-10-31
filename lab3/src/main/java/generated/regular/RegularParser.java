package generated.regular;

import java.io.InputStream;
import java.text.ParseException;

public class RegularParser {

	private RegularLexer lex;

	public void parse(InputStream input) throws ParseException {
		lex = new RegularLexer(input);
		lex.nextToken();
		e1();
	}

	private void s() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM2: {

				if (lex.getCurToken() != RegularToken.TERM2)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				e3();
				

				if (lex.getCurToken() != RegularToken.TERM3)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				break;
			}
			case TERM1: {

				if (lex.getCurToken() != RegularToken.TERM1)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void k() throws ParseException {

		switch (lex.getCurToken()) {
			case EPS: {

				break;
			}
			case TERM3: {

				break;
			}
			case TERM2: {

				break;
			}
			case EOF: {

				break;
			}
			case TERM0: {

				break;
			}
			case TERM1: {

				break;
			}
			case TERM4: {

				if (lex.getCurToken() != RegularToken.TERM4)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				k();
				
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void e1() throws ParseException {

		switch (lex.getCurToken()) {
			case EPS: {


				break;
			}
			case TERM2: {
				c1();
				
				e2();
				

				if (lex.getCurToken() != RegularToken.EOF)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				break;
			}
			case END: {


				break;
			}
			case TERM1: {
				c1();
				
				e2();
				

				if (lex.getCurToken() != RegularToken.EOF)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void e2() throws ParseException {

		switch (lex.getCurToken()) {
			case EPS: {

				break;
			}
			case TERM3: {

				break;
			}
			case EOF: {

				break;
			}
			case TERM0: {

				if (lex.getCurToken() != RegularToken.TERM0)
					throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
				lex.nextToken();
				c1();
				
				e2();
				
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void c1() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM2: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			case TERM1: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void e3() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM2: {
				c1();
				
				e2();
				
				break;
			}
			case TERM1: {
				c1();
				
				e2();
				
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
	private void c2() throws ParseException {

		switch (lex.getCurToken()) {
			case EPS: {

				break;
			}
			case TERM3: {

				break;
			}
			case TERM2: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			case EOF: {

				break;
			}
			case TERM0: {

				break;
			}
			case TERM1: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected token " + lex.getCurString() + " at position: " + (lex.getCurPos() - 1));
		}
	}
}
