package org.cssless.css.parsing;

public class CssToken {

	private final CssTokenType type;
	private final String value;
	private final int index;
	private final int line;
	private final int column;

	private CssToken(CssTokenType type, int index, int line, int column) {
		this.type = type;
		this.value = null;

		this.index = index;
		this.line = line;
		this.column = column;
	}

	private CssToken(CssTokenType type, String value, int index, int line, int column) {
		this.type = type;
		this.value = value;

		this.index = index;
		this.line = line;
		this.column = column;
	}

	public CssTokenType getToken() {
		return this.type;
	}

	public String getValue() {
		return this.value;
	}

	public int getIndex() {
		return index;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(this.type.toString());
		if (this.value != null) {
			buffer.append(": ").append(this.value);
		}
		return buffer.toString();
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof CssToken)) {
			// includes null
			return false;
		}

		CssToken that = (CssToken)arg;
		return
			(this.type.equals(that.type)) &&
			(this.value == null ? that.value == null : this.value.equals(that.value));
	}

	@Override
	public int hashCode() {
		final int HASH_PRIME = 1000003;

		int hash = this.type.hashCode();
		if (this.value != null) {
			hash = hash * HASH_PRIME + this.value.hashCode();
		}
		return hash;
	}

	/* reusable tokens and helper methods */

	static final CssToken start = new CssToken(CssTokenType.START, -1, -1, -1);
	static final CssToken end = new CssToken(CssTokenType.END, -1, -1, -1);

	public static CssToken atRule(String keyword) {
		return new CssToken(CssTokenType.AT_RULE, keyword, -1, -1, -1);
	}

	public static CssToken atRule(String keyword, int index, int line, int column) {
		return new CssToken(CssTokenType.AT_RULE, keyword, index, line, column);
	}

	public static CssToken blockBegin() {
		return new CssToken(CssTokenType.BLOCK_BEGIN, -1, -1, -1);
	}

	public static CssToken blockBegin(int index, int line, int column) {
		return new CssToken(CssTokenType.BLOCK_BEGIN, index, line, column);
	}

	public static CssToken blockEnd() {
		return new CssToken(CssTokenType.BLOCK_END, -1, -1, -1);
	}

	public static CssToken blockEnd(int index, int line, int column) {
		return new CssToken(CssTokenType.BLOCK_END, index, line, column);
	}

	public static CssToken ruleDelim() {
		return new CssToken(CssTokenType.RULE_DELIM, -1, -1, -1);
	}

	public static CssToken ruleDelim(int index, int line, int column) {
		return new CssToken(CssTokenType.RULE_DELIM, index, line, column);
	}

	public static CssToken value(String value) {
		return new CssToken(CssTokenType.VALUE, value, -1, -1, -1);
	}

	public static CssToken value(String value, int index, int line, int column) {
		return new CssToken(CssTokenType.VALUE, value, index, line, column);
	}

	public static CssToken numeric(String value) {
		return new CssToken(CssTokenType.NUMERIC, value, -1, -1, -1);
	}

	public static CssToken numeric(String value, int index, int line, int column) {
		return new CssToken(CssTokenType.NUMERIC, value, index, line, column);
	}

	public static CssToken stringValue(String value) {
		return new CssToken(CssTokenType.STRING, value, -1, -1, -1);
	}

	public static CssToken stringValue(String value, int index, int line, int column) {
		return new CssToken(CssTokenType.STRING, value, index, line, column);
	}

	public static CssToken ident(String value) {
		return new CssToken(CssTokenType.IDENT, value, -1, -1, -1);
	}

	public static CssToken ident(String value, int index, int line, int column) {
		return new CssToken(CssTokenType.IDENT, value, index, line, column);
	}

	public static CssToken important() {
		return new CssToken(CssTokenType.IMPORTANT, -1, -1, -1);
	}

	public static CssToken important(int index, int line, int column) {
		return new CssToken(CssTokenType.IMPORTANT, index, line, column);
	}

	public static CssToken comment(String value) {
		return new CssToken(CssTokenType.COMMENT, value, -1, -1, -1);
	}

	public static CssToken comment(String value, int index, int line, int column) {
		return new CssToken(CssTokenType.COMMENT, value, index, line, column);
	}

	public static CssToken error(String message) {
		return new CssToken(CssTokenType.ERROR, message, -1, -1, -1);
	}

	public static CssToken error(String message, int index, int line, int column) {
		return new CssToken(CssTokenType.ERROR, message, index, line, column);
	}
}
