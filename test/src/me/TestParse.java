package me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestParse {
	private static class MessageParts {
		private final List<String> parts = new ArrayList<String>();
		private final List<String> fields;

		public MessageParts(List<String> fields) {
			this.fields = fields;
		}

		private void addMessage(String msg) {
			System.err.println("Trying to add: " + msg);
			if (parts.size() == fields.size()) {
				throw new IllegalArgumentException(
						"Maxmimun number of parts = " + fields.size());
			}
			parts.add(msg);
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < fields.size(); ++ii) {
				if (parts.size() < ii) {
					sb.append(fields.get(ii)).append(": ")
							.append(parts.get(ii));
				} else {
					break;
				}
			}

			return sb.toString();
		}
	}

	private static String PATTERN = "%d \\{[%t]\\} %l - %m";
	private static Map<String, String> PATTERN_NAMES = new HashMap<String, String>();

	static {
		PATTERN_NAMES.put("%d", "Date");
		PATTERN_NAMES.put("%t", "Thread");
		PATTERN_NAMES.put("%l", "Level");
		PATTERN_NAMES.put("%m", "Message");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String testString = new String(
				"26/04/2013 08:15:23.123 [main] INFO com.me.this.Package - message for you");

		List<String> partNames = parseFormatString(PATTERN);
		MessageParts msgParts = parseLine(partNames, testString);

		System.out.println(msgParts);
	}

	private static List<String> parseFormatString(String pattern) {
		List<String> names = new ArrayList<String>();
		String[] parts = pattern.split(" ");
		for (String part : parts) {
			int idx = part.indexOf("%");
			if (-1 != idx) {
				if (idx + 1 < part.length()) {
					String type = "%" + part.charAt(idx + 1);
					String typeName = PATTERN_NAMES.get(type); // might be null
					names.add(typeName);
				}
			} else {
				names.add(null);
			}
		}
		return names;
	}

	private static MessageParts parseLine(List<String> patternNames, String msg) {
		String[] parts = msg.split(" ");
		MessageParts msgParts = new MessageParts(patternNames);
		for (int ii = 0; ii < parts.length; ii++) {
			if (ii < patternNames.size() - 1) {
				msgParts.addMessage(parts[ii]);
			} else {
				StringBuilder sb = new StringBuilder();
				for (int jj = ii; jj < parts.length; ++jj) {
					sb.append(parts[jj]).append(" ");
				}
				msgParts.addMessage(sb.toString());
			}
		}
		return msgParts;
	}

}
