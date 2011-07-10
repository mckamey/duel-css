package org.cssless.css.ast;

public enum LessBinaryOperatorType {

	/**
	 * Addition (left + right)
	 */
	ADD,

	/**
	 * Subtraction (left - right)
	 */
	SUBTRACT,

	/**
	 * Multiplication (left * right)
	 */
	MULTIPLY,

	/**
	 * Division (left / right)
	 */
	DIVIDE,

	/**
	 * Property access (left[right])
	 */
	ACCESSOR,

	/**
	 * Descendant (left right)
	 */
	DESCENDANT,

	/**
	 * Direct child combinator (left > right)
	 */
	CHILD
}
