package org.cssless.css.ast;

import java.util.*;

public class MultiValueNode extends ValueNode {

	private final List<ValueNode> children = new ArrayList<ValueNode>();

	public MultiValueNode(int index, int line, int column) {
		super(null, index, line, column);
	}

	public MultiValueNode(ValueNode... children) {
		super(null);

		if (children != null) {
			for (ValueNode child : children) {
				this.appendChild(child);
			}
		}
	}

	@Override
	public String getValue() {
		throw new IllegalStateException("Value of MultiValueNode cannot be accessed directly as a String.");
	}

	@Override
	public void setValue(String value) {
		// ignore
	}
	
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int childCount() {
		return this.children.size();
	}

	public List<ValueNode> getChildren() {
		return this.children;
	}

	public CssNode getFirstChild() {
		return this.children.isEmpty() ? null : this.children.get(0);
	}

	public CssNode getLastChild() {
		return this.children.isEmpty() ? null : this.children.get(this.children.size()-1);
	}

	public void appendChild(ValueNode child) {
		// consolidate containers
		if (child instanceof MultiValueNode) {
			for (ValueNode grand : ((MultiValueNode)child).getChildren()) {
				this.appendChild(grand);
			}
			return;
		}

		this.children.add(child);
		child.setParent(this.getParent());
	}

	public boolean removeChild(CssNode oldChild) {
		if (oldChild == null) {
			return false;
		}

		for (int i=0, length=this.children.size(); i<length; i++) {
			CssNode child = this.children.get(i);
			if (child == oldChild) {
				this.children.remove(i);
				child.setParent(null);
				return true;
			}
		}

		return false;
	}

	public boolean replaceChild(ValueNode newChild, ValueNode oldChild) {
		if (oldChild == null) {
			this.appendChild(newChild);
			return true;
		}

		for (int i=0, length=this.children.size(); i<length; i++) {
			CssNode child = this.children.get(i);
			if (child == oldChild) {
				this.children.set(i, newChild);
				newChild.setParent(this.getParent());
				child.setParent(null);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof MultiValueNode) || !this.getClass().equals(arg.getClass())) {
			// includes null
			return false;
		}

		MultiValueNode that = (MultiValueNode)arg;
		if (this.children.size() != that.children.size()) {
			return false;
		}

		for (int i=0, length=this.children.size(); i<length; i++) {
			CssNode a = this.children.get(i);
			CssNode b = that.children.get(i);
			if (a == null ? b != null : !a.equals(b)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int HASH_PRIME = 1000003;

		int hash = 0;
		for (CssNode child : this.children) {
			if (child == null) {
				continue;
			}
			hash = hash * HASH_PRIME + child.hashCode();
		}
		return hash;
	}
}
