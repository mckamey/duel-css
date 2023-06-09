package org.duelengine.css.ast;

/**
 * Represents a string value
 */
public class StringNode extends ValueNode {

	public StringNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public StringNode(String value) {
		super(value);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.STRING;
	}
}
