package me.parsers;

public class StringEndParser implements IMessageParser<String> {

	private final String name;

	public StringEndParser(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParseResult<String> parse(String input, boolean untilEnd) {
		return new ParseResult<String>(input, "");
	}
}
