
import java.io.InputStream;
import java.text.ParseException;

public class NumericParser {

	private NumericLexer lex;

	public void parse(InputStream input) throws ParseException {
		lex = new NumericLexer(input);
		lex.nextToken();
		e1();
	}

	private void s() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM1: {

				lex.nextToken();
				break;
			}
			case TERM2: {

				lex.nextToken();
				e1();
				

				lex.nextToken();
				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private void k() throws ParseException {

		switch (lex.getCurToken()) {
			case END: {

				break;
			}
			case TERM1: {

				break;
			}
			case TERM0: {

				break;
			}
			case TERM3: {

				break;
			}
			case TERM4: {

				lex.nextToken();
				break;
			}
			case TERM2: {

				break;
			}
			case EPS: {

				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private void e1() throws ParseException {

		switch (lex.getCurToken()) {
			case END: {

				break;
			}
			case TERM1: {
				c1();
				
				e2();
				
				break;
			}
			case TERM3: {

				break;
			}
			case TERM2: {
				c1();
				
				e2();
				
				break;
			}
			case EPS: {

				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private void e2() throws ParseException {

		switch (lex.getCurToken()) {
			case END: {

				break;
			}
			case TERM0: {

				lex.nextToken();
				c1();
				
				e2();
				
				break;
			}
			case TERM3: {

				break;
			}
			case EPS: {

				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private void c1() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM1: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			case TERM2: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private void c2() throws ParseException {

		switch (lex.getCurToken()) {
			case END: {

				break;
			}
			case TERM1: {
				s();
				
				k();
				
				c2();
				
				break;
			}
			case TERM0: {

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
			case EPS: {

				break;
			}
			default:
				throw new AssertionError();
		}
	}
}
