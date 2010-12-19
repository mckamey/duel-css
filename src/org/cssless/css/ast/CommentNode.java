package org.cssless.css.ast;

/**
 * Represents an inline comment
 */
public class CommentNode extends ValueNode {

	public CommentNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public CommentNode(String value) {
		super(value);
	}
}
