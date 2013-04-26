package me.parsers;

public class StringNextSpaceParser implements IMessageParser<String> {

	private final String name;

	public StringNextSpaceParser(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		if (true == untilEnd) {
			return new ParseResult<String>(input, "");
		}
		int idx = input.indexOf(" ");
		if (-1 != idx) {
			String rest = idx + 1 >= input.length() ? "" : input.substring(idx + 1);
			return new ParseResult<String>(input.substring(0, idx), rest);
		}
		return new ParseResult<String>(input, "");
	}
}
