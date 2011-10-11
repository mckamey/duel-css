package org.cssless.css.parsing;

import java.util.*;

public final class CssGrammar {

	private static final String KEYWORDS_RESOURCE = "org.cssless.css.parsing.CssKeywords"; //CssKeywords.properties
	private static final String COLOR_RESOURCE = "org.cssless.css.parsing.CssColors"; //CssColors.properties
	private static ResourceBundle colors;
	private static Map<String, Boolean> pseudo;
	private static Map<String, Boolean> atRules;
	private static Map<String, Boolean> vendorPrefixes;
	private static boolean inited;

	// static class
	private CssGrammar() {}

	static final char OP_AT_RULE = '@';
	static final char OP_BLOCK_BEGIN = '{';
	static final char OP_BLOCK_END = '}';
	static final char OP_PAIR_DELIM = ':';
	static final char OP_DECL_DELIM = ';';

	static final char OP_MINUS = '-';
	static final char OP_PLUS = '+';
	static final char OP_PERCENT = '%';
	static final char OP_ESCAPE = '\\';

	static final char OP_STRING_DELIM = '"';
	static final char OP_STRING_DELIM_ALT = '\'';

	static final char OP_STAR = '*';
	static final char OP_HASH = '#';
	static final char OP_DOT = '.';
	static final char OP_ADJACENT = '+';
	static final char OP_CHILD = '>';
	static final char OP_DESCENDANT = ' ';
	static final char OP_SIBLING = '~';

	static final char OP_NAMESPACE_DELIM = '|';
	static final char OP_ATTR_BEGIN = '[';
	static final char OP_ATTR_END = ']';

	static final char OP_MATCH = '=';
	static final char OP_INCLUDES_MATCH = '~';
	static final char OP_DASH_MATCH = '|';
	static final char OP_PREFIX_MATCH = '^';
	static final char OP_SUFFIX_MATCH = '$';
	static final char OP_SUBSTR_MATCH = '*';

	static final char OP_PAREN_BEGIN = '(';
	static final char OP_PAREN_END = ')';
	static final char OP_ITEM_DELIM = ',';

	static final char OP_COMMENT = '/';
	static final String OP_COMMENT_BEGIN = "/*";
	static final String OP_COMMENT_END = "*/";
	static final String OP_COMMENT_ALT_BEGIN = "//";
	static final String OP_COMMENT_ALT_END = "\n";

	static final char OP_IMPORTANT_BEGIN = '!';
	static final String OP_IMPORTANT = "important";

	/**
	 * Checks if keyword is a CSS3 pseudo-class or pseudo-element keyword
	 * @param keyword
	 * @return
	 */
	static boolean isPseudoKeyword(String keyword) {
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

		// filter vendor lookups
		keyword = removeVendorPrefix(keyword);

		return atRules.containsKey(keyword);
	}

	/**
	 * Removes vendor prefix from keyword
	 * @param keyword
	 * @return
	 */
	public static String removeVendorPrefix(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return keyword;
		}

		if (!inited) {
			initLookups();
		}

		if (keyword.charAt(0) == '-') {
			int index = keyword.indexOf('-', 1)+1;
			String prefix = keyword.substring(0, index);
			if (vendorPrefixes.containsKey(prefix)) {
				keyword = keyword.substring(index);
			}
		}

		return keyword;
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

		String[] items;
		Map<String, Boolean> map;

		// definitions maintained in CssKeywords.properties
		ResourceBundle config = ResourceBundle.getBundle(KEYWORDS_RESOURCE);

		// CSS3 pseudo-class keywords
		items = (config != null) && config.containsKey("pseudoClasses") ?
				config.getString("pseudoClasses").split(",") : new String[0];
		map = new HashMap<String, Boolean>(items.length);
		for (String value : items) {
			map.put(value, true);
		}

		// CSS3 pseudo-element keywords
		items = (config != null) && config.containsKey("pseudoElements") ?
				config.getString("pseudoElements").split(",") : new String[0];
		for (String value : items) {
			map.put(value, true);
		}
		pseudo = map;

		// CSS3 at-rule keywords
		items = (config != null) && config.containsKey("atRules") ?
				config.getString("atRules").split(",") : new String[0];
		map = new HashMap<String, Boolean>(items.length);
		for (String value : items) {
			map.put(value, true);
		}
		atRules = map;

		// CSS3 vendor prefixes
		items = (config != null) && config.containsKey("vendorPrefixes") ?
				config.getString("vendorPrefixes").split(",") : new String[0];
		map = new HashMap<String, Boolean>(items.length);
		for (String value : items) {
			map.put(value, true);
		}
		vendorPrefixes = map;
	}
}
