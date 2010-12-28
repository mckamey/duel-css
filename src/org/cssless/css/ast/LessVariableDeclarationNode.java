package org.cssless.css.ast;

/**
 * Represents a LESS variable declaration
 */
public class LessVariableDeclarationNode extends DeclarationNode implements LessNode {

	public LessVariableDeclarationNode(String ident, int index, int line, int column) {
		super(ident, index, line, column);
	}

	public LessVariableDeclarationNode(String ident, ValueNode... expression) {
		super(ident, expression);
	}

	@Override
	public CssNode eval(ContainerNode context) {
		context.putVariable(this);

		// nothing emitted in output
		return null;
	}

	@Override
	protected void filterChild(CssNode child) {
		super.filterChild(child);

		this.requestEval();
	}
}
