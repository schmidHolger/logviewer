package org.hs.logviewer.parser.internal;

import org.hs.logviewer.parser.IMessageParser;

public class StringEndParser implements IMessageParser<String> {

	private final String name;
	private String value;

	public StringEndParser(String name) {
		this.name = name;
	}

	@Override
	public IMessageParser<String> clone() {
		return new StringEndParser(name);
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
		value = input;
		return new ParseResult<String>(input, "");
	}
}
