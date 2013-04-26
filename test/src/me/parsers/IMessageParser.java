package me.parsers;

public interface IMessageParser<T> {
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

	String getName();

	ParseResult<T> parse(String input, boolean untilEnd);
}
