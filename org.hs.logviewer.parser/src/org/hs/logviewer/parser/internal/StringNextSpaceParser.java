package org.hs.logviewer.parser.internal;

import org.hs.logviewer.parser.IMessageParser;

public class StringNextSpaceParser implements IMessageParser<String> {

	private final String name;
	private String value;

	public StringNextSpaceParser(String name) {
		this.name = name;
	}

	@Override
	public IMessageParser<String> clone() {
		return new StringNextSpaceParser(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		if (null == value) {
			throw new IllegalStateException("getValue() called before parsing!");
		}
		return value;
	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		value = "";
		if (true == untilEnd) {
			value = new String(input);
			return new ParseResult<String>(input, "");
		}

		int idx = 0;
		int length = input.length();
		while (input.charAt(idx) != ' ' && idx < length) {
			++idx;
		}
		while (input.charAt(idx) == ' ' && idx < length) {
			++idx;
		}

		if (-1 != idx) {
			String rest = idx == length ? "" : input.substring(idx);
			value = new String(input.substring(0, idx));
			return new ParseResult<String>(value, rest);
		}
		value = new String(input);
		return new ParseResult<String>(input, "");
	}
}
