package org.cssless.css.ast;

/**
 * Represents a numeric value
 */
public class NumericNode extends ValueNode {

	public NumericNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public NumericNode(String value) {
		super(value);
	}
}
