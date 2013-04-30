package org.hs.logviewer.parser;

public interface IMessagePart {
	public void addParser(IMessageParser<?> parser);

	public String getValue(int index);
}
