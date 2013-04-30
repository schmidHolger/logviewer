package org.hs.logviewer.parser.internal;

import java.util.ArrayList;
import java.util.List;

import org.hs.logviewer.parser.IMessageParser;
import org.hs.logviewer.parser.IMessageParser.ParseResult;
import org.hs.logviewer.parser.IMessagePart;

public class MessagePart implements IMessagePart {
	private final List<IMessageParser<?>> parsers = new ArrayList<IMessageParser<?>>();
	private final List<IMessageParser<?>> displayableParsers = new ArrayList<IMessageParser<?>>();
	private final String msg;
	private boolean parsedDone = false;

	public MessagePart(String msg) {
		this.msg = msg;
	}

	@Override
	public void addParser(IMessageParser<?> parser) {
		parsers.add(parser);
	}

	private void doParse() {
		if (false == parsedDone) {
			parsedDone = true;
			String input = new String(msg);
			for (int ii = 0; ii < parsers.size(); ii++) {
				IMessageParser<?> parser = parsers.get(ii);
				ParseResult<?> result = parser.parse(input,
						ii == parsers.size() - 1);

				if (parser.isDisplayable()) {
					displayableParsers.add(parser);
				}
				input = result.getNextPosString();
			}
			parsers.clear();
		}
	}

	@Override
	public String getValue(int index) {
		doParse();
		// System.out.println("getValue(): msg=" + msg);
		// System.out.println("displayableParsers.get(index): "
		// + displayableParsers.get(index));
		return displayableParsers.get(index).getValue();
	}

	@Override
	public String toString() {
		return msg;
	}
}
