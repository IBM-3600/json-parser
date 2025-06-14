package com.smartcube.code.jsonparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private Reader reader;
	private LinkedList<Character> nextCharQueue;
	private Pattern validNumberPattern;
	private static final String validNumberRegxp = "^-?(?:0|[1-9]\\\\d*)(?:\\\\.\\\\d+)?(?:[eE][+-]?\\\\d+)?$";

	public Lexer(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public Lexer(Reader _reader) {
		this.reader = _reader;
		this.nextCharQueue = new LinkedList<>();
		this.validNumberPattern = Pattern.compile(validNumberRegxp);
	}


	public TokenValue next() {
		return null;
	}

	private Character readNext() throws IOException {

		if (this.nextCharQueue.isEmpty()) {
			int cha;
			cha = this.reader.read();
			if (cha < 0)
				return null;
			return (char) cha;
		} else {
			return this.nextCharQueue.poll();
		}

	}

	private Character peekNext() throws IOException {
		if (this.nextCharQueue.isEmpty()) {
			int cha = this.reader.read();
			if (cha < 0)
				return null;
			this.nextCharQueue.add((char) cha);
		}
		return this.nextCharQueue.element();
	}

	private Character peekNext(int pos) throws IOException {
		if (pos == 0) {
			return peekNext();

		}
		while (pos > 0) {
			if (this.nextCharQueue.size() <= pos) {
				return nextCharQueue.get(pos);
			}
		}
		return null;
	}

	private TokenValue parserNextToken() throws IOException {
		Character ch = this.peekNext();
		if (ch == null) {
			return new TokenValue(Token.EOF);
		}
		switch (ch) {
		case '{': {
			this.readNext();
			return new TokenValue(Token.OPEN_OBJECT);
		}

		case '}': {
			this.readNext();
			return new TokenValue(Token.CLOSE_OBJECT);
		}
		case '[': {
			this.readNext();
			return new TokenValue(Token.OPEN_ARRAY);
		}
		case ']': {
			this.readNext();
			return new TokenValue(Token.CLOSE_ARRAY);
		}
		case ',': {
			this.readNext();
			return new TokenValue(Token.COMMA);
		}
		case ':': {
			this.readNext();
			return new TokenValue(Token.COLON);
		}
		case '"': {
			this.readNext();
			String str = this.readString(ch);
			return new TokenValue(Token.STRING, str);
		}
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': {
			this.readNext();
			Number number = this.parseNumber(ch);
			return new TokenValue(Token.NUMBER, number);
		}
		case 't': {
			this.readNext();
			this.parseConstant(ch, "true");
			return new TokenValue(Token.TRUE);
		}
		case 'f': {
			this.readNext();
			this.parseConstant(ch, "false");
			return new TokenValue(Token.FALSE);
		}
		case 'n': {
			this.readNext();
			this.parseConstant(ch, "null");
			return new TokenValue(Token.NULL);
		}
		case '\t':
		case '\r':
		case '\n':
		case ' ': {
			this.readNext();
			this.readWhiteSpace(ch);
			return this.parserNextToken();
		}

		default:
			throw new IOException("invalid character parsing" + ch + "'");
		}

		// return null;
	}

	private void parseConstant(Character ch, String constant) throws IOException {
		String foundConstant = this.readAnyChars(ch, constant);
		if (!constant.equals(foundConstant)) {
			throw new IOException("found invalid token '" + foundConstant + "' instead of '" + constant + "'");
		}

	}

	private Number parseNumber(Character ch) throws IOException {
		String nextNumberString = this.readAnyChars(ch, "0123456789.+-eE");
		Matcher matcher = validNumberPattern.matcher(nextNumberString);
		try {
			if (matcher.matches()) {
				Number number = NumberFormat.getInstance().parse(nextNumberString);
				if (number.getClass().equals(Double.class) && Double.isFinite(number.doubleValue())) {
					return new BigDecimal(nextNumberString);
				}
				return number;
			} else {
				throw new IOException("invalid number format '" + nextNumberString + "'");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private String readString(Character quotes) throws IOException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		Character ch = this.peekNext();
		while (ch != null && !ch.equals(quotes)) {
			if (ch == '\\') {
				sb.append(ch);
				this.readNext();
				ch = peekNext();
				if (this.isControlCharacter(ch)) {
					sb.append(ch);
					this.readNext();
					ch = this.peekNext();
				}
			} else {
				sb.append(ch);
				readNext();
				ch = this.peekNext();
			}
		}
		if (ch == null) {
			throw new IOException("Illegal string parsing - so far = '" + sb + "'");
		}
		readNext();
		return sb.toString();
	}

	private String readWhiteSpace(Character whiteSpace) throws IOException {
		return this.readAnyChars(whiteSpace, " \n\r\t");
	}

	private String readAnyChars(Character any, String nextChar) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(any);
		Character ch = this.peekNext();
		while (ch != null && nextChar.contains(ch.toString())) {
			sb.append(ch);
			readNext();
			ch = this.peekNext();
		}
		if (ch == null) {
			return sb.toString();
		}
		return sb.toString();
	}

	private boolean isControlCharacter(Character ch) {
		// TODO Auto-generated method stub
		return false;
	}

}
