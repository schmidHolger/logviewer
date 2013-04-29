package org.hs.logviewer.parser.internal;

import org.hs.logviewer.parser.IMessageParser;

public class ExceptionParser implements IMessageParser<String> {

	private final StringBuilder messages = new StringBuilder();

	public ExceptionParser(String msg) {
		messages.append(new String(msg));
	}

	public void addMessage(String msg) {
		messages.append("\n").append(msg);
	}

	@Override
	public String getName() {
		return "Exception";
	}

	@Override
	public String getValue() {
		return messages.toString();
	}

	@Override
	public boolean isDisplayable() {
		return true;
	}

	@Override
	public IMessageParser.ParseResult<String> parse(String input,
			boolean untilEnd) {
		return new IMessageParser.ParseResult<String>(messages + input, "");
	}
}
