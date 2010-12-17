package org.cssless.css.parsing;

final class CssGrammar {

	// static class
	private CssGrammar() {}

	public static final char OP_AT_RULE = '@';
	public static final char OP_BLOCK_BEGIN = '{';
	public static final char OP_BLOCK_END = '}';
	public static final char OP_PAIR_DELIM = ':';
	public static final char OP_DECL_DELIM = ';';

	public static final char OP_MINUS = '-';
	public static final char OP_ESCAPE = '\\';

	public static final char OP_STRING_DELIM = '"';
	public static final char OP_STRING_DELIM_ALT = '\'';

	public static final char OP_HASH = '#';
	public static final char OP_DOT = '.';
	public static final char OP_ADJACENT = '+';
	public static final char OP_CHILD = '>';

	public static final char OP_ATTR_BEGIN = '[';
	public static final char OP_ATTR_END = ']';
	public static final char OP_MATCH = '=';
	public static final char OP_INCLUDES_MATCH = '~';
	public static final char OP_DASH_MATCH = '|';
	public static final char OP_PREFIX_MATCH = '^';
	public static final char OP_SUFFIX_MATCH = '$';
	public static final char OP_SUBSTR_MATCH = '*';

	public static final char OP_PAREN_BEGIN = '(';
	public static final char OP_PAREN_END = ')';
	public static final char OP_VALUE_DELIM = ',';

	public static final char OP_COMMENT = '/';
	public static final String OP_COMMENT_BEGIN = "/*";
	public static final String OP_COMMENT_END = "*/";
	public static final String OP_COMMENT_ALT_BEGIN = "//";
	public static final String OP_COMMENT_ALT_END = "\n";

	public static final char OP_IMPORTANT_BEGIN = '!';
	public static final String OP_IMPORTANT = "important";
}
