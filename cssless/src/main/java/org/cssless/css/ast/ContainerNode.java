package org.cssless.css.ast;

import java.util.*;

public class ContainerNode extends CssNode {
	private final List<CssNode> children = new ArrayList<CssNode>();
	private Map<String, LessVariableDeclarationNode> variables;

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

	protected CssNode filterChild(CssNode child) {
		// evaluate any LESS expressions before insertion
		return child.eval(this);
	}

	public void appendChild(CssNode child) {
		child = this.filterChild(child);
		if (child == null) {
			// variable declarations do not generate content
			return;
		}

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

		// evaluate any LESS expressions before insertion
		newChild = this.filterChild(newChild);
		if (newChild == null) {
			return this.removeChild(oldChild);
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

	public boolean hasVariables() {
		return (this.variables != null) && (this.variables.size() > 0);
	}

	public Collection<LessVariableDeclarationNode> getVariables() {
		return (this.variables != null) ? this.variables.values() : null;
	}

	public boolean containsVariable(String name) {
		return (this.variables != null) && this.variables.containsKey(name);
	}
	
	public void putVariable(LessVariableDeclarationNode value) {
		if (this.variables == null) {
			this.variables = new HashMap<String, LessVariableDeclarationNode>();
		}

		this.variables.put(value.getIdent(), value);
	}

	public LessVariableDeclarationNode getVariable(String name) {
		if (this.variables == null || !this.variables.containsKey(name)) {
			return null;
		}

		return this.variables.get(name);
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
