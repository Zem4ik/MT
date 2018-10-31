package generated.expression;
import java.util.HashMap;
import java.io.InputStream;
import java.text.ParseException;

public class ExpressionParser {
HashMap<String, Integer> variables = new HashMap<>();
	private ExpressionLexer lex;

	public void parse(InputStream input) throws ParseException {
		lex = new ExpressionLexer(input);
		lex.nextToken();
		expr();
	}

	private int add() throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM1: {
				int mul = mul();
				
				int add_ = add_(mul);
				val = add_;
				break;
			}
			case TERM2: {
				int mul = mul();
				
				int add_ = add_(mul);
				val = add_;
				break;
			}
			case NUM: {
				int mul = mul();
				
				int add_ = add_(mul);
				val = add_;
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private int add_(int acc) throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM0: {
val = acc;
				break;
			}
			case EPS: {
val = acc;
				break;
			}
			case ADD: {

				assert lex.getCurToken() != ExpressionToken.ADD;
				lex.nextToken();
				int mul = mul();
				int res = acc + mul;
				int add_ = add_(res);
				val = add_;
				break;
			}
			case TERM3: {
val = acc;
				break;
			}
			case MINUS: {

				assert lex.getCurToken() != ExpressionToken.MINUS;
				lex.nextToken();
				int mul = mul();
				int res = acc - mul;
				int add_ = add_(res);
				val = add_;
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private int pow_() throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM0: {
val = 1;
				break;
			}
			case EPS: {
val = 1;
				break;
			}
			case DIV: {
val = 1;
				break;
			}
			case MUL: {
val = 1;
				break;
			}
			case POW: {

				assert lex.getCurToken() != ExpressionToken.POW;
				lex.nextToken();
				int term = term();
				
				int pow_ = pow_();
				val = (int)Math.pow(term, pow_);
				break;
			}
			case ADD: {
val = 1;
				break;
			}
			case TERM3: {
val = 1;
				break;
			}
			case MINUS: {
val = 1;
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private int mul() throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM1: {
				int pow = pow();
				
				int mul_ = mul_(pow);
				val = mul_;
				break;
			}
			case TERM2: {
				int pow = pow();
				
				int mul_ = mul_(pow);
				val = mul_;
				break;
			}
			case NUM: {
				int pow = pow();
				
				int mul_ = mul_(pow);
				val = mul_;
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private String setVar() throws ParseException {

		String result = null;
		switch (lex.getCurToken()) {
			case TERM1: {
				String variable = variable();
				

				assert lex.getCurToken() != ExpressionToken.EQUAL;
				lex.nextToken();
				int add = add();
				int addResult = add;
        variables.put(variable, addResult);
        result = variable + " = " + String.valueOf(addResult);
				break;
			}
			default:
				throw new AssertionError();
		}
		return result;
	}
	private String variable() throws ParseException {

		String name = null;
		switch (lex.getCurToken()) {
			case TERM1: {
name = lex.getCurString();
				assert lex.getCurToken() != ExpressionToken.TERM1;
				lex.nextToken();
				break;
			}
			default:
				throw new AssertionError();
		}
		return name;
	}
	private int mul_(int acc) throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM0: {
val = acc;
				break;
			}
			case EPS: {
val = acc;
				break;
			}
			case DIV: {

				assert lex.getCurToken() != ExpressionToken.DIV;
				lex.nextToken();
				int pow = pow();
				int res = acc / pow;
				int mul_ = mul_(res);
				val = mul_;
				break;
			}
			case MUL: {

				assert lex.getCurToken() != ExpressionToken.MUL;
				lex.nextToken();
				int pow = pow();
				int res = acc * pow;
				int mul_ = mul_(res);
				val = mul_;
				break;
			}
			case ADD: {
val = acc;
				break;
			}
			case TERM3: {
val = acc;
				break;
			}
			case MINUS: {
val = acc;
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private int pow() throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM1: {
				int term = term();
				
				int pow_ = pow_();
				val = (int)Math.pow(term, pow_);
				break;
			}
			case TERM2: {
				int term = term();
				
				int pow_ = pow_();
				val = (int)Math.pow(term, pow_);
				break;
			}
			case NUM: {
				int term = term();
				
				int pow_ = pow_();
				val = (int)Math.pow(term, pow_);
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
	private void expr() throws ParseException {

		switch (lex.getCurToken()) {
			case TERM1: {
				String setVar = setVar();
				System.out.println(setVar);

				assert lex.getCurToken() != ExpressionToken.TERM0;
				lex.nextToken();
				expr();
				
				break;
			}
			case EPS: {

				break;
			}
			case END: {

				break;
			}
			default:
				throw new AssertionError();
		}
	}
	private int term() throws ParseException {

		int val = 0;
		switch (lex.getCurToken()) {
			case TERM1: {
				String variable = variable();
				val = variables.get(variable);
				break;
			}
			case TERM2: {

				assert lex.getCurToken() != ExpressionToken.TERM2;
				lex.nextToken();
				int add = add();
				
val = add;
				assert lex.getCurToken() != ExpressionToken.TERM3;
				lex.nextToken();
				break;
			}
			case NUM: {
val = Integer.parseInt(lex.getCurString());
				assert lex.getCurToken() != ExpressionToken.NUM;
				lex.nextToken();
				break;
			}
			default:
				throw new AssertionError();
		}
		return val;
	}
}
