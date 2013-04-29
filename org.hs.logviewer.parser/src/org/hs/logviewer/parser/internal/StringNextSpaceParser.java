package org.hs.logviewer.parser.internal;

import org.hs.logviewer.parser.IMessageParser;

public class StringNextSpaceParser implements IMessageParser<String> {

	private final String name;
	private String value;

	public StringNextSpaceParser(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		if (true == untilEnd) {
			value = input;
			return new ParseResult<String>(input, "");
		}
		int idx = input.indexOf(" ");
		if (-1 != idx) {
			String rest = idx + 1 >= input.length() ? "" : input
					.substring(idx + 1);
			value = input.substring(0, idx);
			return new ParseResult<String>(value, rest);
		}
		value = input;
		return new ParseResult<String>(input, "");
	}
}
