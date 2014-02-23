package org.duelengine.css.ast;

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

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.LESS_VARIABLE_DECLARATION;
	}

	public ValueNode getValue() {
		int length = childCount();

		if (length == 1) {
			// can safely case as DeclarationNode only allows ValueNode
			return (ValueNode)getFirstChild();
		}

		if (length < 1) {
			return null;
		}

		MultiValueNode multi = new MultiValueNode(getIndex(), getLine(), getColumn());
		ContainerNode container = multi.getContainer();
		for (CssNode child : getChildren()) {
			// TODO: these should be clones in order to maintain correct parent links
			// can safely cast as DeclarationNode only allows ValueNode
			container.appendChild((ValueNode)child);
		}
		return multi;
	}

	@Override
	public CssNode eval(ContainerNode context) {
		setParent(context);
		context.putVariable(this);

		// nothing emitted in output
		return null;
	}
}
