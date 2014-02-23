package org.duelengine.css.ast;

public abstract class ContainerValueNode extends ValueNode {

	private final ContainerNode children;

	public ContainerValueNode(String value, int index, int line, int column) {
		super(value, index, line, column);
		children = new ContainerNode(index, line, column);
		children.setParent(getParent());
	}

	public ContainerValueNode(String value, ValueNode... childNodes) {
		super(value);
		children = new ContainerNode(childNodes);
		children.setParent(getParent());
	}

	@Override
	public abstract CssNodeType getNodeType();

	public ContainerNode getContainer() {
		return children;
	}

	@Override
	void setParent(ContainerNode parent) {
		super.setParent(parent);
		children.setParent(parent);
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof ContainerValueNode) || !this.getClass().equals(arg.getClass())) {
			// includes null
			return false;
		}

		ContainerValueNode that = (ContainerValueNode)arg;
		if (!this.children.equals(that.children)) {
			return false;
		}

		return super.equals(arg);
	}
	
	@Override
	public int hashCode() {
		final int HASH_PRIME = 1000003;

		return super.hashCode() * HASH_PRIME + children.hashCode();
	}
}
