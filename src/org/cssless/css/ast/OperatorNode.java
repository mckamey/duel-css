package org.cssless.css.ast;

/**
 * Represents an operator
 */
public class OperatorNode extends ValueNode {

	public OperatorNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public OperatorNode(String value) {
		super(value);
	}
}
