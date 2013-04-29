package org.hs.logviewer.parser.internal;

import org.hs.logviewer.parser.IMessageParser;

public class DummyParser implements IMessageParser<String> {

	@Override
	public String getName() {
		return "Dummy";
	}

	@Override
	public String getValue() {
		return "";
	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		return new ParseResult<String>("", "");
	}
}
