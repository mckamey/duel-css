package org.cssless.css.parsing;

import java.io.*;
import java.util.*;

/**
 * Processes source text into a token sequence
 */
public class CssLexer implements Iterator<CssToken> {

	private static final int EOF = -1;
	private static final int CAPACITY = 16;

	private final LineNumberReader reader;
	private boolean lineComments;
	private final StringBuilder buffer = new StringBuilder(512);
	private CssToken token = CssToken.start;
	private boolean hasToken;
	private int ch;
	private int index = -1;
	private int column = -1;
	private int line = -1;
	private int token_index = -1;
	private int token_column = -1;
	private int token_line = -1;
	private int mark_ch;
	private int mark_index = -1;
	private int mark_column = -1;
	private int mark_line = -1;
	private Throwable lastError;

	/**
	 * Ctor
	 * @param text
	 */
	public CssLexer(String text) {
		this(new StringReader(text));
	}

	/**
	 * Ctor
	 * @param reader
	 */
	public CssLexer(Reader reader) {
		this.reader = new LineNumberReader(reader);

		try {
			// prime the sequence
			this.nextChar();

		} catch (IOException ex) {
			this.lastError = ex;
			this.token = CssToken.error(ex.getMessage(), this.token_index, this.token_line, this.token_column);
		}
	}

	/**
	 * Gets the current line within the input
	 * @return
	 */
	public int getLine() {
		return this.line;
	}

	/**
	 * Gets the current column within the input
	 * @return
	 */
	public int getColumn() {
		return this.column;
	}

	/**
	 * Gets the current index within the input 
	 * @return
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Returns the last exception encountered
	 */
	public Throwable getLastError() {
		return this.lastError;
	}

	/**
	 * Clears the last error
	 */
	public void clearLastError() {
		this.lastError = null;

		if (this.ensureToken().getToken().equals(CssTokenType.ERROR)) {
			this.token = CssToken.start;
		}
	}

	/**
	 * Gets if line comments are allowed 
	 * @return
	 */
	public boolean allowLineComments() {
		return this.lineComments;
	}

	/**
	 * Sets if line comments are allowed
	 * @param value
	 */
	public void setLineComments(boolean value) {
		this.lineComments = value;
	}

	/**
	 * Determines if any more tokens are available
	 */
	public boolean hasNext() {

		switch (this.ensureToken().getToken()) {
			case END:
			case ERROR:
				// NOTE: cannot pass error until cleared
				return false;
			default:
				return true;
		}
	}

	/**
	 * Returns the next token in the input
	 */
	public CssToken next() {
		try {
			return this.ensureToken();
		} finally {
			this.hasToken = false;
		}
	}

	/**
	 * Altering the input is not supported
	 */
	public void remove()
		throws UnsupportedOperationException {

		throw new UnsupportedOperationException("Not supported");
	}

	/**
	 * Processes the next token in the input
	 * @throws IOException 
	 */
	private CssToken ensureToken() {

		if (this.hasToken) {
			return this.token;
		}

		switch (this.token.getToken()) {
			case END:
			case ERROR:
				// remain in these states
				return this.token;
		}

		boolean isNumber = false;
		try {
			// skip whitespace
			while (CharUtility.isWhiteSpace(this.ch)) {
				this.nextChar();
			}

			this.token_index = this.index;
			this.token_line = this.line;
			this.token_column = this.column;

			switch (this.ch) {
				case CssGrammar.OP_AT_RULE:
					return this.scanAtKeyword();

				case CssGrammar.OP_BLOCK_BEGIN:
					// consume '{'
					this.nextChar();
					return (this.token = CssToken.blockBegin(this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_BLOCK_END:
					// consume '}'
					this.nextChar();
					return (this.token = CssToken.blockEnd(this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_DECL_DELIM:
					// consume ';'
					this.nextChar();
					return (this.token = CssToken.ruleDelim(this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_ITEM_DELIM:
				case CssGrammar.OP_CHILD:
				case CssGrammar.OP_MATCH:
				case CssGrammar.OP_ATTR_BEGIN:
				case CssGrammar.OP_PAREN_BEGIN:
//				case CssGrammar.OP_ATTR_END:
//				case CssGrammar.OP_PAREN_END:
					// consume char
					String value = String.valueOf((char)this.ch);
					this.nextChar();
					return (this.token = CssToken.operator(value, this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_STRING_DELIM:
				case CssGrammar.OP_STRING_DELIM_ALT:
					// consume string
					return this.scanString();

				case CssGrammar.OP_DOT:
					this.setMark(CAPACITY);
					this.nextChar();

					// check if number with leading decimal
					isNumber = CharUtility.isDigit(this.ch);
					this.resetMark();
					break;

				case CssGrammar.OP_MINUS:
					this.setMark(CAPACITY);
					this.nextChar();

					// negative number or identifier
					isNumber = CharUtility.isNumeric(this.ch);
					this.resetMark();
					break;

				case CssGrammar.OP_PLUS:
					// consume '+' adjacent combinator
					this.nextChar();
					return (this.token = CssToken.operator(String.valueOf(CssGrammar.OP_ADJACENT), this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_STAR:
					this.setMark(CAPACITY);
					// consume '*'
					this.nextChar();
					if (this.ch == CssGrammar.OP_MATCH) {
						// consume '='
						this.nextChar();
						String star = String.valueOf(CssGrammar.OP_STAR)+CssGrammar.OP_MATCH;
						return (this.token = CssToken.operator(star, this.token_index, this.token_line, this.token_column));
					}
					this.resetMark();
					break;

				case CssGrammar.OP_SIBLING: // "~" or "~="
				case CssGrammar.OP_PREFIX_MATCH:
				case CssGrammar.OP_SUFFIX_MATCH:
					// consume
					String match = String.valueOf((char)this.ch);
					this.nextChar();
					if (this.ch == CssGrammar.OP_MATCH) {
						this.nextChar();
						match += CssGrammar.OP_MATCH;
					}
					return (this.token = CssToken.operator(match, this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_DASH_MATCH:
					this.setMark(CAPACITY);
					// consume '|'
					this.nextChar();
					if (this.ch == CssGrammar.OP_MATCH) {
						// consume '='
						this.nextChar();
						String star = String.valueOf(CssGrammar.OP_DASH_MATCH)+CssGrammar.OP_MATCH;
						return (this.token = CssToken.operator(star, this.token_index, this.token_line, this.token_column));
					}
					this.resetMark();
					break;

				case CssGrammar.OP_IMPORTANT_BEGIN:
					return this.scanImportant();

				case CssGrammar.OP_COMMENT:
					if (this.tryScanComment()) {
						return this.token;
					}
					this.nextChar();
					return (this.token = CssToken.operator(String.valueOf(CssGrammar.OP_COMMENT), this.token_index, this.token_line, this.token_column));
					
				case EOF:
					return (this.token = CssToken.end);
			}

			if (isNumber || CharUtility.isDigit(this.ch)) {
				return this.scanNumeric();
			}

			return this.scanValue();

		} catch (IOException ex) {
			this.lastError = ex;
			return (this.token = CssToken.error(ex.getMessage(), this.token_index, this.token_line, this.token_column));

		} finally {
			this.hasToken = true;
		}
	}

	/**
	 * Scans the next token as a value
	 * @return
	 * @throws IOException
	 */
	private CssToken scanValue()
		throws IOException {

		// reset the buffer
		this.buffer.setLength(0);

		// certain chars are only valid as leading
		switch (this.ch)
		{
			case CssGrammar.OP_STAR:
				// consume leading char
				this.buffer.append((char)this.ch);
				this.nextChar();
				break;
		}

		while (true) {
			if (CharUtility.isWhiteSpace(this.ch)) {
				// flush the buffer
				return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));
			}

			switch (this.ch) {
				case CssGrammar.OP_HASH:
				case CssGrammar.OP_DOT:
					this.buffer.append((char)this.ch);
					// consume until reach end of name
					this.scanName();
					continue;

				case CssGrammar.OP_BLOCK_END:
				case CssGrammar.OP_BLOCK_BEGIN:
				case CssGrammar.OP_DECL_DELIM:
				case CssGrammar.OP_ITEM_DELIM:

				case CssGrammar.OP_STRING_DELIM:
				case CssGrammar.OP_STRING_DELIM_ALT:

				case CssGrammar.OP_CHILD:
				case CssGrammar.OP_ADJACENT:
				case CssGrammar.OP_SIBLING:
				case CssGrammar.OP_MATCH:
				case CssGrammar.OP_PREFIX_MATCH:
				case CssGrammar.OP_SUFFIX_MATCH:
				case CssGrammar.OP_SUBSTR_MATCH:

				case CssGrammar.OP_IMPORTANT_BEGIN:
				case CssGrammar.OP_COMMENT:
				case EOF:
					// these chars are start of other tokens
					// flush the buffer
					return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_PAREN_END:
				case CssGrammar.OP_ATTR_END:
					if (this.buffer.length() > 0) {
						// these chars are start of next token
						// flush the buffer
						return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));
					}
					// consume until reach a special char
					this.buffer.append((char)this.ch);
					this.nextChar();
					continue;

				case CssGrammar.OP_DASH_MATCH:
					this.setMark(CAPACITY);
					this.nextChar();

					// dashmatch or namspace delim?
					if (this.ch == CssGrammar.OP_MATCH) {
						this.resetMark();
						// start of numeric token
						// flush the buffer
						return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));
					}

					// consume until reach a special char
					this.buffer.append(CssGrammar.OP_DASH_MATCH);
					continue;

				case CssGrammar.OP_PAREN_BEGIN:
				case CssGrammar.OP_ATTR_BEGIN:
					// these chars get appended but signal the end of the token
					this.buffer.append((char)this.ch);
					this.nextChar();

					// flush the buffer
					return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));

				case CssGrammar.OP_PAIR_DELIM:
					this.setMark(CAPACITY);

					int start = this.buffer.length(), shift = 1;
					this.buffer.append(CssGrammar.OP_PAIR_DELIM);

					// check if pseudo-class or pseudo-element
					if (this.nextChar() == CssGrammar.OP_PAIR_DELIM) {
						// potential pseudo-element
						this.buffer.append(CssGrammar.OP_PAIR_DELIM);
						this.nextChar();
						shift++;
					}
					if (CharUtility.isNameStartChar(this.ch)) {
						this.buffer.append((char)this.ch);
						for (int i=1; i<=CAPACITY && CharUtility.isNameChar(this.nextChar()); i++) {
							this.buffer.append((char)this.ch);
						}
					}

					if (CssGrammar.isPseudoKeyword(this.buffer.substring(start+shift))) {
						// continue consuming
						continue;
					}

					this.resetMark();
					this.buffer.setLength(start);

					if (this.buffer.length() == 0) {
						// consume ':'
						this.nextChar();
						return (this.token = CssToken.operator(String.valueOf(CssGrammar.OP_PAIR_DELIM), this.token_index, this.token_line, this.token_column));
					}

					// end of property token
					// flush the buffer
					return (this.token = this.typedValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));

				default:
					// consume until reach a special char
					this.buffer.append((char)this.ch);
					this.nextChar();
					continue;
			}
		}
	}

	/**
	 * Consumes the String literal appending it to the current buffer
	 * @throws IOException
	 */
	private CssToken scanNumeric()
		throws IOException {

		// reset the buffer
		this.buffer.setLength(0);

		// [\.0-9+-]
		this.buffer.append((char)this.ch);

		// consume until reach the delim
		while (CharUtility.isNumeric(this.nextChar())) {
			this.buffer.append((char)this.ch);
		}

		if (this.ch == CssGrammar.OP_PERCENT) {
			this.buffer.append(CssGrammar.OP_PERCENT);
			this.nextChar();
		}

		else if (CharUtility.isNameStartChar(this.ch)) {
			this.scanIdent(true);
		}

		return (this.token = CssToken.numeric(this.buffer.toString(), this.token_index, this.token_line, this.token_column));
	}

	/**
	 * Consumes the next token as a String literal
	 * @throws IOException
	 */
	private CssToken scanString()
		throws IOException {

		// reset the buffer
		this.buffer.setLength(0);
		int delim = this.ch;
		this.buffer.append((char)delim);

		// consume until reach the delim
		while (this.nextChar() != delim) {
			this.buffer.append((char)this.ch);

			if (this.ch == CssGrammar.OP_ESCAPE) {
				if (!CharUtility.isEscape(this.nextChar()) && !CharUtility.isNewline(this.ch)) {
					throw new SyntaxException("Malformed escape sequence", this.token_index, this.token_line, this.token_column);
				}
				this.buffer.append((char)this.ch);

			} else if (!CharUtility.isStringChar(this.ch) &&
					this.ch != CssGrammar.OP_STRING_DELIM &&
					this.ch != CssGrammar.OP_STRING_DELIM_ALT) {
				throw new SyntaxException("Unterminated string value", this.token_index, this.token_line, this.token_column);
			}
		}

		this.nextChar();
		this.buffer.append((char)delim);
		return (this.token = CssToken.stringValue(this.buffer.toString(), this.token_index, this.token_line, this.token_column));
	}

	/**
	 * Scan the next token as an At-Keyword
	 * @return
	 * @throws IOException
	 */
	private CssToken scanAtKeyword()
		throws IOException {

		// consume '@'
		this.nextChar();

		return (this.token = CssToken.atRule(this.scanIdent(false), this.token_index, this.token_line, this.token_column));
	}

	private CssToken scanImportant()
		throws IOException {

		// consume '!' and any whitespace
		while (CharUtility.isWhiteSpace(this.nextChar()));

		// mark position with capacity to check start delims
		this.setMark(CAPACITY);

		for (int i=0, length=CssGrammar.OP_IMPORTANT.length(); i<length; i++) {
			if (this.ch != CssGrammar.OP_IMPORTANT.charAt(i)) {
				// didn't match important
				// NOTE: this may throw an exception if block was unterminated
				this.resetMark();
				return (this.token = this.typedValue("!", this.token_index, this.token_line, this.token_column));
			}

			this.nextChar();
		}

		return (this.token = CssToken.important(this.token_index, this.token_line, this.token_column));
	}

	/**
	 * Scan the next token as an identifier
	 * @return identifier
	 * @throws IOException
	 */
	private String scanIdent(boolean append)
		throws IOException {

		if (!append) {
			this.buffer.setLength(0);
		}

		// optional ident prefix
		if (this.ch == CssGrammar.OP_MINUS) {
			this.buffer.append((char)this.ch);
			this.nextChar();
		}

		if (!CharUtility.isNameStartChar(this.ch)) {
			return this.buffer.toString();
		}

		// consume ident
		this.buffer.append((char)this.ch);
		if (this.ch == CssGrammar.OP_ESCAPE) {
			if (!CharUtility.isEscape(this.nextChar())) {
				throw new SyntaxException("Malformed escape sequence", this.token_index, this.token_line, this.token_column);
			}
			this.buffer.append((char)this.ch);
		}

		return this.scanName();
	}

	private String scanName() throws IOException {
		while (CharUtility.isNameChar(this.nextChar())) {
			this.buffer.append((char)this.ch);
			if (this.ch == CssGrammar.OP_ESCAPE) {
				if (!CharUtility.isEscape(this.nextChar())) {
					throw new SyntaxException("Malformed escape sequence", this.token_index, this.token_line, this.token_column);
				}
				this.buffer.append((char)this.ch);
			}
		}
		return this.buffer.toString();
	}

	/**
	 * Tries to scan the next token as a comment
	 * @return
	 * @throws IOException 
	 */
	private boolean tryScanComment()
		throws IOException {

		// mark current position with capacity to check start delims
		this.setMark(CAPACITY);

		String value = this.tryScanBlockValue(CssGrammar.OP_COMMENT_BEGIN, CssGrammar.OP_COMMENT_END);
		if (value == null) {
			// NOTE: this may throw an exception if block was unterminated
			this.resetMark();

			if (this.lineComments) {
				this.setMark(CAPACITY);
				value = this.tryScanBlockValue(CssGrammar.OP_COMMENT_ALT_BEGIN, CssGrammar.OP_COMMENT_ALT_END);

				if (value == null) {
					// NOTE: this may throw an exception if block was unterminated
					this.resetMark();
					return false;
				}
			} else {
				return false;
			}
		}

		this.token = CssToken.comment(value, this.token_index, this.token_line, this.token_column);
		return true;
	}

	/**
	 * Tries to scan the next token as an unparsed value
	 * @param begin
	 * @param end
	 * @return
	 * @throws IOException
	 */
	private String tryScanBlockValue(String begin, String end)
		throws IOException {

		for (int i=0, length=begin.length(); i<length; i++) {
			if (this.ch != begin.charAt(i)) {
				// didn't match begin delim
				return null;
			}

			this.nextChar();
		}

		// reset the buffer, mark start
		this.buffer.setLength(0);

		for (int i=0, length=end.length(); this.ch != EOF; ) {
			// check each char
			if (this.ch == end.charAt(i)) {
				// move to next char
				i++;
				if (i >= length) {
					length--;

					// consume final char
					this.nextChar();

					// trim ending delim from buffer
					this.buffer.setLength(this.buffer.length() - length);
					return this.buffer.toString();
				}
			} else {
				// reset to start of delim
				i = 0;
			}

			this.buffer.append((char)this.ch);
			this.nextChar();
		}

		if ("\n".equals(end)) {
			// EOF is allowed terminator
			return this.buffer.toString();
		}

		throw new SyntaxException("Unterminated block", this.token_index, this.token_line, this.token_column);
	}

	private CssToken typedValue(String value, int index, int line, int column) {
		int length = value != null ? value.length()-1 : -1;
		if ((length == 3 || length == 6) && value.charAt(0) == CssGrammar.OP_HASH) {
			while (length > 0) {
				if (!CharUtility.isHexDigit(value.charAt(length))) {
					break;
				}

				length--;
			}

			if (length == 0) {
				return CssToken.color(value, index, line, column);
			}
		}

		if (CharUtility.isOperator(value)) {
			return CssToken.operator(value, index, line, column);

		} else if (CssGrammar.decodeColor(value) != null) {
			return CssToken.color(value, index, line, column);
		}

		int last = value != null ? value.length()-1 : -1;
		if (last > 0) {
			switch (value.charAt(last)) {
				case CssGrammar.OP_PAREN_BEGIN:
					return CssToken.func(value.substring(0, last), index, line, column);
				case CssGrammar.OP_ATTR_BEGIN:
					return CssToken.accessor(value.substring(0, last), index, line, column);
			}
		}
		return CssToken.value(value, index, line, column);
	}

	/**
	 * Gets the next character in the input and updates statistics
	 * @return
	 * @throws IOException
	 */
	private int nextChar() throws IOException {
		int prevLine = this.line;

		this.ch = this.reader.read();
		this.line = this.reader.getLineNumber();

		// update statistics
		if (prevLine != this.line) {
			this.column = 0;
		} else {
			this.column++;
		}
		this.index++;

		return this.ch;
	}

	/**
	 * Marks the input location to enable resetting
	 * @param bufferSize
	 * @throws IOException
	 */
	private void setMark(int bufferSize) throws IOException {
		// store current statistics
		this.mark_line = this.line;
		this.mark_column = this.column;
		this.mark_index = this.index;
		this.mark_ch = this.ch;

		this.reader.mark(bufferSize);
	}

	/**
	 * Resets the input location to the marked location
	 * @throws IOException
	 */
	private void resetMark() throws IOException {
		// restore current statistics
		this.line = this.mark_line;
		this.column = this.mark_column;
		this.index = this.mark_index;
		this.ch = this.mark_ch;

		this.reader.reset();
	}

	/**
	 * Produces a list of the remaining tokens
	 * @return
	 */
	public ArrayList<CssToken> toList() {

		ArrayList<CssToken> list = new ArrayList<CssToken>();
		while (this.hasNext()) {
			list.add(this.next());
		}
		return list;
	}
}
