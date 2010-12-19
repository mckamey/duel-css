package org.cssless.css.parsing;

import org.cssless.css.ast.CssNode;

/**
 * Represents compilation errors related to a specific node in the resulting AST
 */
@SuppressWarnings("serial")
public class InvalidNodeException extends SyntaxException {

	private final CssNode node;

	public InvalidNodeException(String message, CssNode node) {
		super(message,
			(node != null) ? node.getIndex() : -1,
			(node != null) ? node.getLine() : -1,
			(node != null) ? node.getColumn() : -1);

		this.node = node;
	}

	public InvalidNodeException(String message, CssNode node, Throwable cause) {
		super(message,
			(node != null) ? node.getIndex() : -1,
			(node != null) ? node.getLine() : -1,
			(node != null) ? node.getColumn() : -1,
			cause);

		this.node = node;
	}

	public InvalidNodeException(String message, int index, int line, int column, CssNode node, Throwable cause) {
		super(message, index, line, column, cause);

		this.node = node;
	}

	public CssNode getNode() {
		return this.node;
	}
}
