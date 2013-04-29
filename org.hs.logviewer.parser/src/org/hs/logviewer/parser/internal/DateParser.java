package org.hs.logviewer.parser.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.hs.logviewer.parser.IMessageParser;

public class DateParser implements IMessageParser<String> {

	private static DateFormat DF = new SimpleDateFormat();
	private final String name;
	private String value;

	public DateParser(String name) {
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
		int idx = -1;
		for (int ii = 0; ii < 4; ++ii) {
			idx = input.indexOf(" ", idx + 1);
		}

		value = input.substring(0, idx);
		String rest = idx + 1 >= input.length() ? "" : input.substring(idx + 1);
		return new ParseResult<String>(value, rest);
	}
}
