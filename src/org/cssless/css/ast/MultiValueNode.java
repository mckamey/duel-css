package org.cssless.css.ast;

public class MultiValueNode extends ContainerValueNode {

	public MultiValueNode(int index, int line, int column) {
		super(null, index, line, column);
	}

	public MultiValueNode(ValueNode... children) {
		super(null, children);
	}

	@Override
	public String getValue() {
		throw new IllegalStateException("Value of MultiValueNode cannot be accessed directly as a String.");
	}

	@Override
	public void setValue(String value) {
		// ignore
	}
}
