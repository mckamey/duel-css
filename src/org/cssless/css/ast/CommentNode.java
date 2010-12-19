package org.cssless.css.ast;

/**
 * Represents an inline comment
 */
public class CommentNode extends ValueNode {

	private static final String BEGIN = "/*"; 
	private static final String END = "*/"; 

	public CommentNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public CommentNode(String value) {
		super(value);
	}

	@Override
	public String toString() {
		return BEGIN + super.toString() + END;
	}
}
