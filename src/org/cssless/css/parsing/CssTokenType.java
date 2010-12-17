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
	 * Value
	 */
	VALUE,

	/**
	 * Identifier
	 */
	IDENT,

	/**
	 * At-rule
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
