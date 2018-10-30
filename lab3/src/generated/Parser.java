
import java.io.InputStream;
import java.text.ParseException;

public class Parser {

	private Lexer lex;

	public void parse(InputStream input) throws ParseException {
		lex = new Lexer(input);
		lex.nextToken();
		grammar();
	}

	private void grammar() throws ParseException {

		switch (lex.getCurToken()) {
			default:
				throw new AssertionError();
		}
	}
}
