package generated.arithmetic;
import java.io.*;
import java.text.ParseException;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) {
		ArithmeticParser parser = new ArithmeticParser();
		int res;
		try {
			if (args.length != 0) {
				try {
					res = parser.parse(new ByteArrayInputStream(args[0].getBytes(StandardCharsets.UTF_8.name())));
					System.out.println(res);
				} catch (UnsupportedEncodingException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			} else {
				try {
					InputStream input = new FileInputStream(new File("inputs/arithmetic_input.txt"));
					res = parser.parse(input);
					System.out.println(res);
				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		} catch (ParseException e) {
			System.err.println("Parser failed: \nCause: " + e.getMessage() + e.getErrorOffset());
			System.exit(1);
		}
	}
}