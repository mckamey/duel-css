package org.duelengine.css.ast;

/**
 * Function invocation
 */
public class FunctionNode extends ContainerValueNode {

	public FunctionNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public FunctionNode(String value, ValueNode... args) {
		super(value, args);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.FUNCTION;
	}
}
