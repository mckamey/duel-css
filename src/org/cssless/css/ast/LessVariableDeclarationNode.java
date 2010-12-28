package org.cssless.css.ast;

/**
 * Represents a LESS variable declaration
 */
public class LessVariableDeclarationNode extends DeclarationNode {

	public LessVariableDeclarationNode(String ident, int index, int line, int column) {
		super(ident, index, line, column);
	}

	public LessVariableDeclarationNode(String ident, ValueNode... expression) {
		super(ident, expression);
	}

	public ValueNode getValue() {
		int length = this.childCount();

		if (length == 1) {
			// can safely case as DeclarationNode only allows ValueNode
			return (ValueNode)this.getFirstChild();
		}

		if (length < 1) {
			return null;
		}

		MultiValueNode multi = new MultiValueNode(this.getIndex(), this.getLine(), this.getColumn());
		for (CssNode child : this.getChildren()) {
			// can safely case as DeclarationNode only allows ValueNode
			multi.appendChild((ValueNode)child);
		}
		return multi;
	}
	
	@Override
	public CssNode eval(ContainerNode context) {
		context.putVariable(this);

		// nothing emitted in output
		return null;
	}
}
