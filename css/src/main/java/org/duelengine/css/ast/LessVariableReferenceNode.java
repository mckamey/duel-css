package org.duelengine.css.ast;

/**
 * Represents a LESS variable reference
 */
public class LessVariableReferenceNode extends ValueNode {

	public LessVariableReferenceNode(String varRef, int index, int line, int column) {
		super(varRef, index, line, column);
	}

	public LessVariableReferenceNode(String varRef) {
		super(varRef);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.LESS_VARIABLE_REFERENCE;
	}

	@Override
	public CssNode eval(ContainerNode context) {
		String varRef = getValue();
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

		return varDecl.getValue();
	}
}
