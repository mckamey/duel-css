package org.cssless.css.parsing;

/**
 * http://www.w3.org/TR/css3-syntax/#style
 * http://www.w3.org/TR/css3-syntax/#grammar0
 * http://www.w3.org/TR/css3-syntax/#tokenization
 */
public enum CssTokenType {

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
	 * Function begin
	 */
	FUNCTION,

	/**
	 * Accessor begin
	 */
	ACCESSOR,

	/**
	 * Operator
	 */
	OPERATOR,

	/**
	 * General value
	 */
	VALUE,

	/**
	 * String value
	 */
	STRING,

	/**
	 * Numeric value
	 */
	NUMERIC,

	/**
	 * Color value
	 */
	COLOR,

	/**
	 * Priority flag
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
