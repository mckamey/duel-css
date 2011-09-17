package org.cssless.css.ast;

/**
 * Represents CSS3 Combinators
 * http://www.w3.org/TR/css3-selectors/#combinators
 */
public enum CombinatorType {

	/**
	 * Self (no space or word break)
	 */
	SELF,

	/**
	 * Descendant (space)
	 */
	DESCENDANT,

	/**
	 * Direct child (greater-than)
	 */
	CHILD,

	/**
	 * Adjacent sibling (plus)
	 */
	ADJACENT,

	/**
	 * General sibling (tilde)
	 */
	SIBLING
}
