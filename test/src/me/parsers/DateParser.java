package me.parsers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateParser implements IMessageParser<String> {

	private static DateFormat DF = new SimpleDateFormat();
	private final String name;

	public DateParser(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		int idx = input.indexOf(" ");
		if (-1 != idx) {
			idx = input.indexOf(" ", idx + 1);
			if (-1 != idx) {
				String dateString = input.substring(0, idx);
				String rest = idx + 1 >= input.length() ? "" : input.substring(idx + 1);
				return new ParseResult<String>(dateString, rest);
			}
		}
		return null;
	}
}
