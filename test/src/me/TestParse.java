package me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.parsers.DateParser;
import me.parsers.IMessageParser;
import me.parsers.IMessageParser.ParseResult;
import me.parsers.NullParser;
import me.parsers.StringNextSpaceParser;

public class TestParse {

	private static class MessageParts {
		private final List<String> parts = new ArrayList<String>();
		private final List<IMessageParser<?>> parsers;

		public MessageParts(List<IMessageParser<?>> parsers) {
			this.parsers = parsers;
		}

		private void addMessage(String msg) {
			System.err.println("Trying to add: " + msg);
			if (parts.size() == parsers.size()) {
				throw new IllegalArgumentException("Maxmimun number of parts = " + parsers.size());
			}
			parts.add(msg);
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < parsers.size(); ++ii) {
				if (ii < parts.size()) {
					if (ii != 0) {
						sb.append("\n");
					}
					sb.append(parsers.get(ii).getName()).append(": ").append(parts.get(ii));
				} else {
					break;
				}
			}

			return sb.toString();
		}
	}

	private static String PATTERN = "%d \\{[%t]\\} %l - %m";
	private static Map<String, IMessageParser> PATTERN_NAMES = new HashMap<String, IMessageParser>();

	static {
		PATTERN_NAMES.put("%d", new DateParser("Date"));
		PATTERN_NAMES.put("%t", new StringNextSpaceParser("Thread"));
		PATTERN_NAMES.put("%l", new StringNextSpaceParser("Level"));
		PATTERN_NAMES.put("%m", new StringNextSpaceParser("Message"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String testString = new String("26.04.2013 08:15:23.123 [main] INFO com.me.this.Package - message for you");

		List<IMessageParser<?>> partNames = parseFormatString(PATTERN);
		MessageParts msgParts = parseLine(partNames, testString);

		System.out.println(msgParts.toString());
	}

	private static List<IMessageParser<?>> parseFormatString(String pattern) {
		List<IMessageParser<?>> names = new ArrayList<IMessageParser<?>>();
		String[] parts = pattern.split(" ");
		for (String part : parts) {
			int idx = part.indexOf("%");
			if (-1 != idx) {
				if (idx + 1 < part.length()) {
					String type = "%" + part.charAt(idx + 1);
					// might be null
					IMessageParser<?> typeName = PATTERN_NAMES.get(type);
					if (null == typeName) {
						names.add(new NullParser());
					}
					names.add(typeName);
				}
			} else {
				names.add(new NullParser());
			}
		}
		return names;
	}

	private static MessageParts parseLine(List<IMessageParser<?>> parsers, String msg) {
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
