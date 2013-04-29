package org.hs.logviewer.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.hs.logviewer.parser.internal.ExceptionParser;
import org.hs.logviewer.parser.internal.MessagePart;
import org.hs.logviewer.parser.internal.ParserFactory;

public class ParserEngine {
	private final List<IMessageParser<?>> parsers;
	private final boolean inException = false;
	private ExceptionParser exceptionParser;
	private final File msgFile;
	private ArrayList<IMessagePart> parts;

	public ParserEngine(String pattern, File msgFile) {
		parsers = ParserFactory.parseFormatString(pattern);
		this.msgFile = msgFile;
	}

	public List<IMessagePart> parse() {
		parts = new ArrayList<IMessagePart>();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(msgFile);
			br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				IMessagePart parsedLine = parseLine(line);
				if (null != parsedLine) {
					parts.add(parsedLine);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return parts;
	}

	public IMessagePart parseLine(String msg) {
		String input = msg;
		MessagePart msgParts = new MessagePart(msg);
		for (int ii = 0; ii < parsers.size(); ii++) {
			IMessageParser<?> parser = parsers.get(ii);
			if (0 == ii) {
				try {
					parser.parse(input, ii == parsers.size() - 1);
					msgParts.addParser(parser);
					exceptionParser = null;
				} catch (Exception e) {
					if (null == exceptionParser) {
						exceptionParser = ParserFactory.getExceptionParser(msg);
						for (int jj = 0; jj < parsers.size() - 1; ++jj) {
							if (parsers.get(jj).isDisplayable()) {
								msgParts.addParser(ParserFactory
										.getDummyParser());
							}
						}
						msgParts.addParser(exceptionParser);
						return msgParts; // do not add any more
					} else {
						exceptionParser.addMessage(msg);
					}
					return null; // do not add any more
				}
			} else {
				msgParts.addParser(parser);
			}
		}
		return msgParts;
	}
}
