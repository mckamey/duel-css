package org.duelengine.css.parsing;

/**
 * Provides simplified definitions of character classes
 */
final class CharUtility {

	public static boolean isNullOrWhiteSpace(String value) {
		if (value == null) {
			return true;
		}

		for (int i=0, length=value.length(); i<length; i++) {
			if (!isWhiteSpace(value.charAt(i))) {
				return false;
			}
		}
		
		return true;
	}

	public static boolean isNewline(int ch) {
		switch (ch) {
			case '\n':		// LF
			case '\r':		// CR
			case '\u000C':	// FF
				return true;
			default:
				return false;
		}
	}

	public static boolean isOperator(String value) {
		if (value != null) {
			switch (value.length()) {
				case 1:
					switch (value.charAt(0)) {
						case CssGrammar.OP_PAIR_DELIM:
						case CssGrammar.OP_ITEM_DELIM:
						case CssGrammar.OP_CHILD:
						case CssGrammar.OP_ADJACENT:
						case CssGrammar.OP_SIBLING:
						case CssGrammar.OP_MATCH:
						case CssGrammar.OP_PAREN_BEGIN:
						case CssGrammar.OP_PAREN_END:
						case CssGrammar.OP_ATTR_BEGIN:
						case CssGrammar.OP_ATTR_END:
						case CssGrammar.OP_NAMESPACE_DELIM:
						case CssGrammar.OP_IMPORTANT_BEGIN:
						case '/':
						case '-':
							return true;
					}
					break;
				case 2:
					if ("~=".equals(value) ||
						"|=".equals(value) ||
						"^=".equals(value) ||
						"$=".equals(value)) {
						return true;
					}
					break;
			}
		}

		return false;
	}

	public static boolean isWhiteSpace(int ch) {
		switch (ch) {
			case ' ':		// Space
			case '\t':		// Tab
			case '\n':		// LF
			case '\r':		// CR
			case '\u000C':	// FF
				return true;
			default:
				return false;
		}
	}

	public static boolean isLetter(int ch) {
		return
			((ch >= 'a') && (ch <= 'z')) ||
			((ch >= 'A') && (ch <= 'Z'));
	}

	public static boolean isDigit(int ch) {
		return (ch >= '0') && (ch <= '9');
	}

	public static boolean isNumeric(int ch) {
		return ((ch >= '0') && (ch <= '9')) || ch == '.';
	}

	public static boolean isHexDigit(int ch) {
		return
			(ch >= '0' && ch <= '9') ||
			(ch >= 'a' && ch <= 'f') ||
			(ch >= 'A' && ch <= 'F');
	}

	public static boolean isEscape(int ch) {
		return
			(ch >= '\u0020' && ch <= '\u007E') ||
			(ch >= '\u0080' && ch <= '\uD7FF') ||
			(ch >= '\uE000' && ch <= '\uFFFD');
			//(ch >= '\u10000' && ch <= '\u10FFFF');
	}

	public static boolean isNonAscii(int ch) {
		return
			(ch >= '\u0080' && ch <= '\uD7FF') ||
			(ch >= '\uE000' && ch <= '\uFFFD');
			//(ch >= '\u10000' && ch <= '\u10FFFF');
	}

	public static boolean isUrlChar(int ch) {
		return
			(ch == '\\') || // need to check isEscape(next)
			(ch == '\u0009') ||
			(ch == '\u0021') ||
			(ch >= '\u0023' && ch <= '\u007E') ||
			CharUtility.isNonAscii(ch);
	}

	public static boolean isStringChar(int ch) {
		return
			// can also escape newline chars
			(ch == '\u0020') ||
			CharUtility.isUrlChar(ch);
	}

	/**
	 * Checks for CSS name start char
	 */
	public static boolean isNameStartChar(int ch) {
		// http://www.w3.org/TR/css3-syntax/#SUBTOK-nmstart
		return
			(ch >= 'a' && ch <= 'z') ||
			(ch >= 'A' && ch <= 'Z') ||
			(ch == '_') ||
			(ch == '\\') || // need to check isEscape(next)
			CharUtility.isNonAscii(ch);
	}

	/**
	 * Checks for CSS name char
	 */
	public static boolean isNameChar(int ch)
	{
		// http://www.w3.org/TR/css3-syntax/#SUBTOK-nmchar
		return
			CharUtility.isNameStartChar(ch) ||
			(ch >= '0' && ch <= '9') ||
			(ch == '-');
	}
}
