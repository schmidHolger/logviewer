package org.hs.logviewer.parser;

public interface IMessagePart {
	public void addParser(IMessageParser<?> parser);

	public Object getValue(int index);
}
