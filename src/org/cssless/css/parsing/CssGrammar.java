package org.cssless.css.parsing;

import java.util.*;

final class CssGrammar {

	private static final String KEYWORDS_RESOURCE = "org.cssless.css.parsing.CssKeywords"; //CssKeywords.properties
	private static final String COLOR_RESOURCE = "org.cssless.css.parsing.CssColors"; //CssColors.properties
	private static ResourceBundle colors;
	private static Map<String, Boolean> pseudo;
	private static Map<String, Boolean> atRules;
	private static boolean inited;

	// static class
	private CssGrammar() {}

	public static final char OP_AT_RULE = '@';
	public static final char OP_BLOCK_BEGIN = '{';
	public static final char OP_BLOCK_END = '}';
	public static final char OP_PAIR_DELIM = ':';
	public static final char OP_DECL_DELIM = ';';

	public static final char OP_MINUS = '-';
	public static final char OP_PLUS = '+';
	public static final char OP_PERCENT = '%';
	public static final char OP_ESCAPE = '\\';

	public static final char OP_STRING_DELIM = '"';
	public static final char OP_STRING_DELIM_ALT = '\'';

	public static final char OP_STAR = '*';
	public static final char OP_HASH = '#';
	public static final char OP_DOT = '.';
	public static final char OP_ADJACENT = '+';
	public static final char OP_CHILD = '>';
	public static final char OP_DESCENDANT = ' ';
	public static final char OP_SIBLING = '~';

	public static final char OP_NAMESPACE_DELIM = '|';
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
	public static final char OP_ITEM_DELIM = ',';

	public static final char OP_COMMENT = '/';
	public static final String OP_COMMENT_BEGIN = "/*";
	public static final String OP_COMMENT_END = "*/";
	public static final String OP_COMMENT_ALT_BEGIN = "//";
	public static final String OP_COMMENT_ALT_END = "\n";

	public static final char OP_IMPORTANT_BEGIN = '!';
	public static final String OP_IMPORTANT = "important";

	/**
	 * Checks if keyword is a CSS3 pseudo-class or pseudo-element keyword
	 * @param keyword
	 * @return
	 */
	public static boolean isPseudoKeyword(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return false;
		}

		if (!inited) {
			initLookups();
		}

		return pseudo.containsKey(keyword);
	}

	/**
	 * Checks if keyword is a CSS3 at-rule keyword 
	 * @param keyword
	 * @return
	 */
	public static boolean isAtRuleKeyword(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return false;
		}

		if (!inited) {
			initLookups();
		}

		return atRules.containsKey(keyword);
	}

	/**
	 * Decodes CSS3 color keywords into hex values
	 * @param name
	 * @return
	 */
	public static String decodeColor(String keyword) {

		if (keyword == null || keyword.isEmpty()) {
			return null;
		}

		if (colors == null) {
			// CSS3 color keywords
			colors = ResourceBundle.getBundle(COLOR_RESOURCE);
		}

		if (colors.containsKey(keyword)) {
			return colors.getString(keyword);
		}

		return null;
	}

	private static void initLookups() {

		String[] tags;
		Map<String, Boolean> map;

		// definitions maintained in CssKeywords.properties
		ResourceBundle config = ResourceBundle.getBundle(KEYWORDS_RESOURCE);

		// CSS3 pseudo-class keywords
		tags = (config != null) && config.containsKey("pseudoClasses") ?
				config.getString("pseudoClasses").split(",") : new String[0];
		map = new HashMap<String, Boolean>(tags.length);
		for (String value : tags) {
			map.put(value, true);
		}

		// CSS3 pseudo-element keywords
		tags = (config != null) && config.containsKey("pseudoElements") ?
				config.getString("pseudoElements").split(",") : new String[0];
		for (String value : tags) {
			map.put(value, true);
		}
		pseudo = map;

		// CSS3 at-rule keywords
		tags = (config != null) && config.containsKey("atRules") ?
				config.getString("Rules").split(",") : new String[0];
		map = new HashMap<String, Boolean>(tags.length);
		for (String value : tags) {
			map.put(value, true);
		}
		atRules = map;
	}
}
