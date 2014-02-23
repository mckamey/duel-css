package org.duelengine.css.ast;

public class AccessorNode extends ContainerValueNode {

	public AccessorNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public AccessorNode(String value, ValueNode... args) {
		super(value, args);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.ACCESSOR;
	}
}
