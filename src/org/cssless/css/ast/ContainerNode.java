package org.cssless.css.ast;

import java.util.*;

public class ContainerNode extends CssNode {
	private final List<CssNode> children = new ArrayList<CssNode>();

	public ContainerNode(int index, int line, int column) {
		super(index, line, column);
	}

	protected ContainerNode(CssNode... children) {
		if (children != null) {
			for (CssNode child : children) {
				this.appendChild(child);
			}
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int childCount() {
		return this.children.size();
	}

	public List<CssNode> getChildren() {
		return this.children;
	}

	public CssNode getFirstChild() {
		return this.children.isEmpty() ? null : this.children.get(0);
	}

	public CssNode getLastChild() {
		return this.children.isEmpty() ? null : this.children.get(this.children.size()-1);
	}

	public void appendChild(CssNode child) {
		this.children.add(child);
		child.setParent(this);
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

	public boolean replaceChild(CssNode newChild, CssNode oldChild) {
		if (oldChild == null) {
			this.appendChild(newChild);
			return true;
		}

		for (int i=0, length=this.children.size(); i<length; i++) {
			CssNode child = this.children.get(i);
			if (child == oldChild) {
				this.children.set(i, newChild);
				newChild.setParent(this);
				child.setParent(null);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof ContainerNode) || !this.getClass().equals(arg.getClass())) {
			// includes null
			return false;
		}

		ContainerNode that = (ContainerNode)arg;
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
