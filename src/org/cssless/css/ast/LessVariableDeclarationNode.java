package org.cssless.css.ast;

import org.cssless.css.codegen.ArithmeticEvaluator;

/**
 * Represents a LESS variable declaration
 */
public class LessVariableDeclarationNode extends DeclarationNode implements LessNode {

	private static final ArithmeticEvaluator evaluator = new ArithmeticEvaluator();

	public LessVariableDeclarationNode(String ident, int index, int line, int column) {
		super(ident, index, line, column);
	}

	public LessVariableDeclarationNode(String ident, ValueNode... expression) {
		super(ident, expression);
	}

	@Override
	public ValueNode eval(ContainerNode context) {
		ValueNode result = evaluator.eval(this.getChildren());
		context.putVariable(this.getIdent(), result);

		// nothing emitted in output
		return null;
	}
}
