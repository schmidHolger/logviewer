package org.hs.logviewer.parser;

public interface IMessageParser<T> extends Cloneable {
	public static class ParseResult<T> {
		private final String nextPosString;
		private final T val;

		public ParseResult(T val, String nextPosString) {
			super();
			this.val = val;
			this.nextPosString = nextPosString;
		}

		public String getNextPosString() {
			return nextPosString;
		}

		public T getValue() {
			return val;
		}
	}

	public IMessageParser<T> clone();

	String getName();

	String getValue();

	boolean isDisplayable();

	ParseResult<T> parse(String input, boolean untilEnd);
}
