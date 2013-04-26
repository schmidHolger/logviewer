package me.parsers;

public class NullParser implements IMessageParser<String> {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public me.parsers.IMessageParser.ParseResult<String> parse(String input, boolean untilEnd) {
		if (true == untilEnd) {
			return new ParseResult<String>(null, "");
		}

		int idx = input.indexOf(" ");
		idx = input.indexOf(" ", idx + 1);
		String rest = idx + 1 >= input.length() ? "" : input.substring(idx + 1);
		return new ParseResult<String>(null, rest);
	}
}
