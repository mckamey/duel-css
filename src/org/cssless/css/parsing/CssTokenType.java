package org.cssless.css.parsing;

/**
 * http://www.w3.org/TR/css3-syntax/#style
 * http://www.w3.org/TR/css3-syntax/#grammar0
 * http://www.w3.org/TR/css3-syntax/#tokenization
 */
public enum CssTokenType {

	/**
	 * Value
	 */
	START,

	/**
	 * General value
	 */
	VALUE,

	/**
	 * Identifier value
	 */
	IDENT,

	/**
	 * String value
	 */
	STRING,

	/**
	 * Numeric value
	 */
	NUMERIC,

	/**
	 * At-rule keyword
	 */
	AT_RULE,

	/**
	 * Block begin 
	 */
	BLOCK_BEGIN,

	/**
	 * Block end
	 */
	BLOCK_END,

	/**
	 * Rule delimiter
	 */
	RULE_DELIM,

	/**
	 * Comma delimiter
	 */
	ITEM_DELIM,

	/**
	 * Operator
	 */
	OPERATOR,

	/**
	 * Priority marker
	 */
	IMPORTANT,

	/**
	 * Comment block
	 */
	COMMENT,

	/**
	 * Error state
	 */
	ERROR,

	/**
	 * End of file
	 */
	END
}
