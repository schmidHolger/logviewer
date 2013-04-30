package org.hs.logviewer.parser.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hs.logviewer.parser.IMessageParser;

public class ParserFactory {
	private static Map<String, IMessageParser<?>> PATTERN_NAMES = new HashMap<String, IMessageParser<?>>();

	static {
		PATTERN_NAMES.put("%d", new DateParser("Date"));
		PATTERN_NAMES.put("%t", new StringNextSpaceParser("Thread"));
		PATTERN_NAMES.put("%l", new StringNextSpaceParser("Logger"));
		PATTERN_NAMES.put("%c", new StringNextSpaceParser("Level"));
		PATTERN_NAMES.put("%m", new StringNextSpaceParser("Message"));
	}

	public static IMessageParser<String> getDummyParser() {
		return new DummyParser();
	}

	public static ExceptionParser getExceptionParser(String msg) {
		return new ExceptionParser(msg);
	}

	public static List<IMessageParser<?>> parseFormatString(String pattern) {
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
}
