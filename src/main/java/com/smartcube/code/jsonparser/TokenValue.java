package com.smartcube.code.jsonparser;

public class TokenValue {
	private final Token token;
	private final Object value;
	private final String string;

	public TokenValue(Token _token) {
		this(_token, null);
	}

	public TokenValue(Token _token, Object _value) {
		this.token = _token;
		this.value = _value;
		this.string = this.value == null ? "" : this.value.toString();

	}
}
