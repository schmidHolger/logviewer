package me;

import me.parsers.ParserEngine;

public class TestParse {

	private static String PATTERN = "%d \\{[%t]\\} %l - %m";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String testString = new String("26.04.2013 08:15:23.123 [main] INFO com.me.this.Package - message for you");
		ParserEngine engine = new ParserEngine(PATTERN);
		MessageParts msgParts = engine.parseLine(testString);
		System.out.println(msgParts.toString());
	}

}
