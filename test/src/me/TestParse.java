package me;

import me.parsers.ParserEngine;

public class TestParse {

	private static String PATTERN = "%d \\{[%t]\\} %l - %m";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] testStrings = new String[] { //
		//
				new String("26.04.2013 08:16:23.123 [main] INFO com.me.this.Package - message1 for you"), //
				new String("26.04.2013 08:17:23.123 [main] INFO com.me.this.Package - message2 for you"), //
				new String("26.04.2013 08:18:23.123 [main] INFO com.me.this.Package - message3 for you"), //
				new String("26.04.2013 08:19:23.123 [main] INFO com.me.this.Package - message4 for you"), //
				new String("Exception in thread \"main\" java.lang.NullPointerException"), //
				new String("	at me.parsers.ParserEngine.parseLine(ParserEngine.java:22)"), //
				new String("	at me.TestParse.main(TestParse.java:23)"), //
		};
		ParserEngine engine = new ParserEngine(PATTERN);
		for (String string : testStrings) {
			MessageParts msgParts = engine.parseLine(string);
			System.out.println(msgParts.toString());
		}
	}
}
