package org.cssless.css.ast;

public abstract class ContainerValueNode extends ValueNode {

	private final ContainerNode children;

	public ContainerValueNode(String value, int index, int line, int column) {
		super(value, index, line, column);
		this.children = new ContainerNode();
	}

	public ContainerValueNode(String value, ValueNode... children) {
		super(value);
		this.children = new ContainerNode(children);
	}

	public ContainerNode getContainer() {
		return this.children;
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

		return super.hashCode() * HASH_PRIME + this.children.hashCode();
	}
}
