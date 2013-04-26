package me;

import java.util.ArrayList;
import java.util.List;

import me.parsers.IMessageParser;

public class MessageParts {
	private final List<String> parts = new ArrayList<String>();
	private final List<IMessageParser<?>> parsers;

	public MessageParts(List<IMessageParser<?>> parsers) {
		this.parsers = parsers;
	}

	public void addMessage(String msg) {
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
