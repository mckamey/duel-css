package org.duelengine.css.parsing;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.duelengine.css.parsing.CssParser.Syntax;

/**
 * Processes source text into a token sequence
 */
public class CssLexer implements Iterator<CssToken> {

	private static final int EOF = -1;
	private static final int CAPACITY = 16;

	private final Syntax syntax;
	private final LineNumberReader reader;
	private final StringBuilder buffer = new StringBuilder(512);
	private CssToken token = CssToken.start;
	private boolean hasToken;
	private int ch;
	private int ch_index = -1;
	private int ch_line = -1;
	private int ch_column = -1;
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
		this(new StringReader(text), null);
	}

	/**
	 * Ctor
	 * @param text
	 * @param allowLineComments
	 */
	public CssLexer(String text, Syntax lexSyntax) {
		this(new StringReader(text), lexSyntax);
	}

	/**
	 * Ctor
	 * @param source
	 */
	public CssLexer(Reader source) {
		this(source, null);
	}

	/**
	 * Ctor
	 * @param source
	 * @param allowLineComments
	 */
	public CssLexer(Reader source, Syntax lexSyntax) {
		syntax = (lexSyntax == null) ? Syntax.CSS : lexSyntax;

		reader = (source instanceof LineNumberReader) ?
			(LineNumberReader)source :
			new LineNumberReader(source);

		try {
			// prime the sequence
			nextChar();

		} catch (IOException ex) {
			lastError = ex;
			token = CssToken.error(ex.getMessage(), token_index, token_line, token_column);
		}
	}

	/**
	 * Gets the current line within the input
	 * @return
	 */
	public int getLine() {
		return ch_line;
	}

	/**
	 * Gets the current column within the input
	 * @return
	 */
	public int getColumn() {
		return ch_column;
	}

	/**
	 * Gets the current index within the input 
	 * @return
	 */
	public int getIndex() {
		return ch_index;
	}

	/**
	 * Returns the last exception encountered
	 */
	public Throwable getLastError() {
		return lastError;
	}

	/**
	 * Clears the last error
	 */
	public void clearLastError() {
		lastError = null;

		if (ensureToken().getToken().equals(CssTokenType.ERROR)) {
			token = CssToken.start;
		}
	}

	/**
	 * Gets the syntax used 
	 * @return
	 */
	public Syntax syntax() {
		return syntax;
	}

	/**
	 * Determines if any more tokens are available
	 */
	public boolean hasNext() {

		switch (ensureToken().getToken()) {
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
			return ensureToken();
		} finally {
			hasToken = false;
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

		if (hasToken) {
			return token;
		}

		switch (token.getToken()) {
			case END:
			case ERROR:
				// remain in these states
				return token;
			default:
				break;
		}

		boolean isNumber = false;
		try {
			// skip whitespace
			while (CharUtility.isWhiteSpace(ch)) {
				nextChar();
			}

			token_index = ch_index;
			token_line = ch_line;
			token_column = ch_column;

			switch (ch) {
				case CssGrammar.OP_AT_RULE:
					return scanAtKeyword();

				case CssGrammar.OP_BLOCK_BEGIN:
					// consume '{'
					nextChar();
					return (token = CssToken.blockBegin(token_index, token_line, token_column));

				case CssGrammar.OP_BLOCK_END:
					// consume '}'
					nextChar();
					return (token = CssToken.blockEnd(token_index, token_line, token_column));

				case CssGrammar.OP_DECL_DELIM:
					// consume ';'
					nextChar();
					return (token = CssToken.ruleDelim(token_index, token_line, token_column));

				case CssGrammar.OP_ITEM_DELIM:
				case CssGrammar.OP_CHILD:
				case CssGrammar.OP_MATCH:
				case CssGrammar.OP_ATTR_BEGIN:
				case CssGrammar.OP_PAREN_BEGIN:
//				case CssGrammar.OP_ATTR_END:
//				case CssGrammar.OP_PAREN_END:
					// consume char
					String value = String.valueOf((char)ch);
					nextChar();
					return (token = CssToken.operator(value, token_index, token_line, token_column));

				case CssGrammar.OP_STRING_DELIM:
				case CssGrammar.OP_STRING_DELIM_ALT:
					// consume string
					return scanString();

				case CssGrammar.OP_DOT:
					setMark(CAPACITY);
					nextChar();

					// check if number with leading decimal
					isNumber = CharUtility.isDigit(ch);
					resetMark();
					break;

				case CssGrammar.OP_MINUS:
					setMark(CAPACITY);
					nextChar();

					// negative number or identifier
					isNumber = CharUtility.isNumeric(ch);
					resetMark();
					break;

				case CssGrammar.OP_PLUS:
					// consume '+' adjacent combinator
					nextChar();
					return (token = CssToken.operator(String.valueOf(CssGrammar.OP_ADJACENT), token_index, token_line, token_column));

				case CssGrammar.OP_STAR:
					setMark(CAPACITY);
					// consume '*'
					nextChar();
					if (ch == CssGrammar.OP_MATCH) {
						// consume '='
						nextChar();
						String star = String.valueOf(CssGrammar.OP_STAR)+CssGrammar.OP_MATCH;
						return (token = CssToken.operator(star, token_index, token_line, token_column));
					}
					resetMark();
					break;

				case CssGrammar.OP_SIBLING: // "~" or "~="
				case CssGrammar.OP_PREFIX_MATCH:
				case CssGrammar.OP_SUFFIX_MATCH:
					// consume
					String match = String.valueOf((char)ch);
					nextChar();
					if (ch == CssGrammar.OP_MATCH) {
						nextChar();
						match += CssGrammar.OP_MATCH;
					}
					return (token = CssToken.operator(match, token_index, token_line, token_column));

				case CssGrammar.OP_DASH_MATCH:
					setMark(CAPACITY);
					// consume '|'
					nextChar();
					if (ch == CssGrammar.OP_MATCH) {
						// consume '='
						nextChar();
						String star = String.valueOf(CssGrammar.OP_DASH_MATCH)+CssGrammar.OP_MATCH;
						return (token = CssToken.operator(star, token_index, token_line, token_column));
					}
					resetMark();
					break;

				case CssGrammar.OP_IMPORTANT_BEGIN:
					return scanImportant();

				case CssGrammar.OP_COMMENT:
					if (tryScanComment()) {
						return token;
					}
					nextChar();
					return (token = CssToken.operator(String.valueOf(CssGrammar.OP_COMMENT), token_index, token_line, token_column));
					
				case EOF:
					return (token = CssToken.end);
			}

			if (isNumber || CharUtility.isDigit(ch)) {
				return scanNumeric();
			}

			return scanValue();

		} catch (IOException ex) {
			lastError = ex;
			return (token = CssToken.error(ex.getMessage(), token_index, token_line, token_column));

		} finally {
			hasToken = true;
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
		buffer.setLength(0);

		// certain chars are only valid as leading
		switch (ch)
		{
			case CssGrammar.OP_STAR:
				// consume leading char
				buffer.append((char)ch);
				nextChar();
				break;
		}

		while (true) {
			if (CharUtility.isWhiteSpace(ch)) {
				// flush the buffer
				return (token = typedValue(buffer.toString(), token_index, token_line, token_column));
			}

			switch (ch) {
				case CssGrammar.OP_HASH:
				case CssGrammar.OP_DOT:
					buffer.append((char)ch);
					// consume until reach end of name
					scanName();
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
					return (token = typedValue(buffer.toString(), token_index, token_line, token_column));

				case CssGrammar.OP_PAREN_END:
				case CssGrammar.OP_ATTR_END:
					if (buffer.length() > 0) {
						// these chars are start of next token
						// flush the buffer
						return (token = typedValue(buffer.toString(), token_index, token_line, token_column));
					}
					// consume until reach a special char
					buffer.append((char)ch);
					nextChar();
					continue;

				case CssGrammar.OP_DASH_MATCH:
					setMark(CAPACITY);
					nextChar();

					// dashmatch or namspace delim?
					if (ch == CssGrammar.OP_MATCH) {
						resetMark();
						// start of numeric token
						// flush the buffer
						return (token = typedValue(buffer.toString(), token_index, token_line, token_column));
					}

					// consume until reach a special char
					buffer.append(CssGrammar.OP_DASH_MATCH);
					continue;

				case CssGrammar.OP_PAREN_BEGIN:
				case CssGrammar.OP_ATTR_BEGIN:
					// these chars get appended but signal the end of the token
					buffer.append((char)ch);
					nextChar();

					// flush the buffer
					return (token = typedValue(buffer.toString(), token_index, token_line, token_column));

				case CssGrammar.OP_PAIR_DELIM:
					setMark(CAPACITY);

					int start = buffer.length(), shift = 1;
					buffer.append(CssGrammar.OP_PAIR_DELIM);

					// check if pseudo-class or pseudo-element
					if (nextChar() == CssGrammar.OP_PAIR_DELIM) {
						// potential pseudo-element
						buffer.append(CssGrammar.OP_PAIR_DELIM);
						nextChar();
						shift++;
					}
					if (CharUtility.isNameStartChar(ch)) {
						buffer.append((char)ch);
						for (int i=1; i<=CAPACITY && CharUtility.isNameChar(nextChar()); i++) {
							buffer.append((char)ch);
						}
					}

					if (CssGrammar.isPseudoKeyword(buffer.substring(start+shift))) {
						// continue consuming
						continue;
					}

					resetMark();
					buffer.setLength(start);

					if (buffer.length() == 0) {
						// consume ':'
						nextChar();
						return (token = CssToken.operator(String.valueOf(CssGrammar.OP_PAIR_DELIM), token_index, token_line, token_column));
					}

					// end of property token
					// flush the buffer
					return (token = typedValue(buffer.toString(), token_index, token_line, token_column));

				default:
					// consume until reach a special char
					buffer.append((char)ch);
					nextChar();
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
		buffer.setLength(0);

		// [\.0-9+-]
		buffer.append((char)ch);

		// consume until reach the delim
		while (CharUtility.isNumeric(nextChar())) {
			buffer.append((char)ch);
		}

		if (ch == CssGrammar.OP_PERCENT) {
			buffer.append(CssGrammar.OP_PERCENT);
			nextChar();
		}

		else if (CharUtility.isNameStartChar(ch)) {
			scanIdent(true);
		}

		return (token = CssToken.numeric(buffer.toString(), token_index, token_line, token_column));
	}

	/**
	 * Consumes the next token as a String literal
	 * @throws IOException
	 */
	private CssToken scanString()
		throws IOException {

		// reset the buffer
		buffer.setLength(0);
		int delim = ch;
		buffer.append((char)delim);

		// consume until reach the delim
		while (nextChar() != delim) {
			buffer.append((char)ch);

			if (ch == CssGrammar.OP_ESCAPE) {
				if (!CharUtility.isEscape(nextChar()) && !CharUtility.isNewline(ch)) {
					throw new SyntaxException("Malformed escape sequence", token_index, token_line, token_column);
				}
				buffer.append((char)ch);

			} else if (!CharUtility.isStringChar(ch) &&
					ch != CssGrammar.OP_STRING_DELIM &&
					ch != CssGrammar.OP_STRING_DELIM_ALT) {
				throw new SyntaxException("Unterminated string value", token_index, token_line, token_column);
			}
		}

		nextChar();
		buffer.append((char)delim);
		return (token = CssToken.stringValue(buffer.toString(), token_index, token_line, token_column));
	}

	/**
	 * Scan the next token as an At-Keyword
	 * @return
	 * @throws IOException
	 */
	private CssToken scanAtKeyword()
		throws IOException {

		// consume '@'
		nextChar();

		return (token = CssToken.atRule(scanIdent(false), token_index, token_line, token_column));
	}

	private CssToken scanImportant()
		throws IOException {

		// consume '!' and any whitespace
		while (CharUtility.isWhiteSpace(nextChar()));

		// mark position with capacity to check start delims
		setMark(CAPACITY);

		for (int i=0, length=CssGrammar.OP_IMPORTANT.length(); i<length; i++) {
			if (ch != CssGrammar.OP_IMPORTANT.charAt(i)) {
				// didn't match important
				// NOTE: this may throw an exception if block was unterminated
				resetMark();
				return (token = typedValue("!", token_index, token_line, token_column));
			}

			nextChar();
		}

		return (token = CssToken.important(token_index, token_line, token_column));
	}

	/**
	 * Scan the next token as an identifier
	 * @return identifier
	 * @throws IOException
	 */
	private String scanIdent(boolean append)
		throws IOException {

		if (!append) {
			buffer.setLength(0);
		}

		// optional ident prefix
		if (ch == CssGrammar.OP_MINUS) {
			buffer.append((char)ch);
			nextChar();
		}

		if (!CharUtility.isNameStartChar(ch)) {
			return buffer.toString();
		}

		// consume ident
		buffer.append((char)ch);
		if (ch == CssGrammar.OP_ESCAPE) {
			if (!CharUtility.isEscape(nextChar())) {
				throw new SyntaxException("Malformed escape sequence", token_index, token_line, token_column);
			}
			buffer.append((char)ch);
		}

		return scanName();
	}

	private String scanName() throws IOException {
		while (CharUtility.isNameChar(nextChar())) {
			buffer.append((char)ch);
			if (ch == CssGrammar.OP_ESCAPE) {
				if (!CharUtility.isEscape(nextChar())) {
					throw new SyntaxException("Malformed escape sequence", token_index, token_line, token_column);
				}
				buffer.append((char)ch);
			}
		}
		return buffer.toString();
	}

	/**
	 * Tries to scan the next token as a comment
	 * @return
	 * @throws IOException 
	 */
	private boolean tryScanComment()
		throws IOException {

		// mark current position with capacity to check start delims
		setMark(CAPACITY);

		String value = tryScanBlockValue(CssGrammar.OP_COMMENT_BEGIN, CssGrammar.OP_COMMENT_END);
		if (value == null) {
			// NOTE: this may throw an exception if block was unterminated
			resetMark();

			if (syntax == Syntax.LESS) {
				// allow C++ style line comments
				setMark(CAPACITY);
				value = tryScanBlockValue(CssGrammar.OP_COMMENT_ALT_BEGIN, CssGrammar.OP_COMMENT_ALT_END);

				if (value == null) {
					// NOTE: this may throw an exception if block was unterminated
					resetMark();
					return false;
				}

			} else {
				return false;
			}
		}

		token = CssToken.comment(value, token_index, token_line, token_column);
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
			if (ch != begin.charAt(i)) {
				// didn't match begin delim
				return null;
			}

			nextChar();
		}

		// reset the buffer, mark start
		buffer.setLength(0);

		for (int i=0, length=end.length(); ch != EOF; ) {
			// check each char
			if (ch == end.charAt(i)) {
				// move to next char
				i++;
				if (i >= length) {
					length--;

					// consume final char
					nextChar();

					// trim ending delim from buffer
					buffer.setLength(buffer.length() - length);
					return buffer.toString();
				}
			} else {
				// reset to start of delim
				i = 0;
			}

			buffer.append((char)ch);
			nextChar();
		}

		if ("\n".equals(end)) {
			// EOF is allowed terminator
			return buffer.toString();
		}

		throw new SyntaxException("Unterminated block", token_index, token_line, token_column);
	}

	private static CssToken typedValue(String value, int index, int line, int column) {
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
		if (last >= 0) {
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
		int prevLine = ch_line;

		ch = reader.read();
		ch_line = reader.getLineNumber();

		// update statistics
		if (prevLine != ch_line) {
			ch_column = 0;
		} else {
			ch_column++;
		}
		ch_index++;

		return ch;
	}

	/**
	 * Marks the input location to enable resetting
	 * @param bufferSize
	 * @throws IOException
	 */
	private void setMark(int bufferSize) throws IOException {
		// store current statistics
		mark_line = ch_line;
		mark_column = ch_column;
		mark_index = ch_index;
		mark_ch = ch;

		reader.mark(bufferSize);
	}

	/**
	 * Resets the input location to the marked location
	 * @throws IOException
	 */
	private void resetMark() throws IOException {
		// restore current statistics
		ch_line = mark_line;
		ch_column = mark_column;
		ch_index = mark_index;
		ch = mark_ch;

		reader.reset();
	}

	/**
	 * Produces a list of the remaining tokens
	 * @return
	 */
	public ArrayList<CssToken> toList() {

		ArrayList<CssToken> list = new ArrayList<CssToken>();
		while (hasNext()) {
			list.add(next());
		}
		return list;
	}

	/**
	 * Properly String-decodes the value
	 * @return
	 */
	public static String decodeString(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}

		final int length = value.length();
		StringBuilder buffer = new StringBuilder(length);
		char delim = value.charAt(0);
		if (delim != CssGrammar.OP_STRING_DELIM &&
			delim != CssGrammar.OP_STRING_DELIM_ALT) {
			return value;
		}

		// consume until reach the delim
		char ch;
		for (int i=1; i<length && (ch = value.charAt(i)) != delim; i++) {
			if (ch == CssGrammar.OP_ESCAPE) {
				i++;
				if (i >= length) {
					throw new SyntaxException("Unterminated string value", i, 1, i+1);
				}
				ch = value.charAt(i);
				if (!CharUtility.isEscape(ch) && !CharUtility.isNewline(ch)) {
					throw new SyntaxException("Malformed escape sequence", i, 1, i+1);
				}
				buffer.append(ch);

			} else if (!CharUtility.isStringChar(ch) &&
					ch != CssGrammar.OP_STRING_DELIM &&
					ch != CssGrammar.OP_STRING_DELIM_ALT) {
				throw new SyntaxException("Unterminated string value", i, 1, i+1);
			} else {
				buffer.append(ch);
			}
		}

		return buffer.toString();
	}

	/**
	 * Properly String-encodes the value
	 * @param value
	 */
	public static String encodeString(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}

		final int length = value.length();
		StringBuilder buffer = new StringBuilder(2*length);

		buffer.append(CssGrammar.OP_STRING_DELIM);
		for (int i=0; i<length; i++) {
			char ch = value.charAt(i);
			if (!CharUtility.isStringChar(ch)) {
				buffer.append(CssGrammar.OP_ESCAPE);
			}
			buffer.append(ch);
		}
		buffer.append(CssGrammar.OP_STRING_DELIM);

		return buffer.toString();
	}
}
