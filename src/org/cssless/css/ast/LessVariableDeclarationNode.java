package org.cssless.css.ast;

import java.util.*;

import org.cssless.css.parsing.InvalidNodeException;

/**
 * Represents a LESS variable declaration
 */
public class LessVariableDeclarationNode extends ContainerNode implements LessNode {

	private String name;

	public LessVariableDeclarationNode(String name, int index, int line, int column) {
		super(index, line, column);

		this.name = name;
	}

	public LessVariableDeclarationNode(String name, ValueNode... expression) {
		super(expression);

		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	@Override
	public void appendChild(CssNode value) {
		if (!(value instanceof ValueNode)) {
			throw new InvalidNodeException("LESS variables may only hold value expressions", value);
		}

		super.appendChild(value);
	}

	@Override
	public ValueNode eval(ContainerNode context) {
		ValueNode result = null;
		for (CssNode child : this.getChildren()) {
			// TODO: build LESS expression tree

			// need parens node for forcing precendence
			// and multi-value node (comma list)
		}
		context.putVariable(this.name, result);

		// nothing emitted in output
		return null;
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof LessVariableDeclarationNode)) {
			// includes null
			return false;
		}

		LessVariableDeclarationNode that = (LessVariableDeclarationNode)arg;
		if (this.name == null ? that.name != null : !this.name.equals(that.name)) {
			return false;
		}

		return super.equals(arg);
	}
}
