package me.parsers;

import java.util.List;

import me.MessageParts;
import me.parsers.IMessageParser.ParseResult;

public class ParserEngine {
	private final List<IMessageParser<?>> parsers;

	public ParserEngine(String pattern) {
		parsers = ParserFactory.parseFormatString(pattern);
	}

	public MessageParts parseLine(String msg) {
		String[] parts = msg.split(" ");
		String input = msg;
		MessageParts msgParts = new MessageParts(parsers);
		for (int ii = 0; ii < parsers.size(); ii++) {
			IMessageParser<?> parser = parsers.get(ii);
			ParseResult<?> result = parser.parse(input, ii == parsers.size() - 1);
			input = result.getNextPosString();
			Object value = result.getValue();
			if (null != value) {
				msgParts.addMessage(value.toString());
			} else {
				msgParts.addMessage(null);
			}
		}
		return msgParts;
	}
}
