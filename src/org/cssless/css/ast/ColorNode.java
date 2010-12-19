package org.cssless.css.ast;

/**
 * Represents a color value
 */
public class ColorNode extends ValueNode {

	public ColorNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public ColorNode(String value) {
		super(value);
	}
}
