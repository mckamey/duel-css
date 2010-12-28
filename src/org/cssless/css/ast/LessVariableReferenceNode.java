package org.cssless.css.ast;

import org.cssless.css.codegen.ArithmeticEvaluator;

/**
 * Represents a LESS variable reference
 */
public class LessVariableReferenceNode extends ValueNode implements LessNode {

	private static final ArithmeticEvaluator evaluator = new ArithmeticEvaluator();

	public LessVariableReferenceNode(String varRef, int index, int line, int column) {
		super(varRef, index, line, column);
	}

	public LessVariableReferenceNode(String varRef) {
		super(varRef);
	}

	@Override
	public ValueNode eval(ContainerNode context) {
		String varRef = this.getValue();
		if (varRef == null || varRef.isEmpty()) {
			throw new NullPointerException("varRef");
		}

		// walk up scope stack
		while (context != null && !context.containsVariable(varRef)) {
			context = context.getParent();
		}

		if (context == null) {
			throw new IllegalStateException("Undeclared variable reference: @"+varRef);
		}

		LessVariableDeclarationNode varDecl = context.getVariable(varRef);
		if (varDecl == null) {
			throw new IllegalStateException("Undeclared variable reference: @"+varRef);
		}

		ValueNode value = evaluator.eval(varDecl.getChildren());
		if (value == null) {
			throw new IllegalStateException("Undeclared variable reference: @"+varRef);
		}
		return value;
	}
}
