package org.duelengine.css.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerNode extends CssNode {
	private final List<CssNode> children = new ArrayList<CssNode>();
	private Map<String, LessVariableDeclarationNode> variables;

	public ContainerNode(int index, int line, int column) {
		super(index, line, column);
	}

	protected ContainerNode(CssNode... children) {
		if (children != null) {
			for (CssNode child : children) {
				appendChild(child);
			}
		}
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.CONTAINER;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public int childCount() {
		return children.size();
	}

	public List<CssNode> getChildren() {
		return children;
	}

	public CssNode getFirstChild() {
		return children.isEmpty() ? null : children.get(0);
	}

	public CssNode getLastChild() {
		return children.isEmpty() ? null : children.get(children.size()-1);
	}

	protected CssNode filterChild(CssNode child) {
		// evaluate any LESS expressions before insertion
		return child.eval(this);
	}

	public void appendChild(CssNode child) {
		child = filterChild(child);
		if (child == null) {
			// variable declarations do not generate content
			return;
		}

		children.add(child);
		child.setParent(this);
	}

	public boolean removeChild(CssNode oldChild) {
		if (oldChild == null) {
			return false;
		}

		for (int i=0; i<children.size(); i++) {
			CssNode child = children.get(i);
			if (child == oldChild) {
				children.remove(i);
				child.setParent(null);
				return true;
			}
		}

		return false;
	}

	public boolean replaceChild(CssNode newChild, CssNode oldChild) {
		if (oldChild == null) {
			appendChild(newChild);
			return true;
		}

		// evaluate any LESS expressions before insertion
		newChild = filterChild(newChild);
		if (newChild == null) {
			return removeChild(oldChild);
		}

		for (int i=0; i<children.size(); i++) {
			CssNode child = children.get(i);
			if (child == oldChild) {
				children.set(i, newChild);
				newChild.setParent(this);
				child.setParent(null);
				return true;
			}
		}

		return false;
	}

	public boolean hasVariables() {
		return (variables != null) && (variables.size() > 0);
	}

	public Collection<LessVariableDeclarationNode> getVariables() {
		return (variables != null) ? variables.values() : null;
	}

	public boolean containsVariable(String name) {
		return (variables != null) && variables.containsKey(name);
	}
	
	public void putVariable(LessVariableDeclarationNode value) {
		if (variables == null) {
			variables = new HashMap<String, LessVariableDeclarationNode>();
		}

		variables.put(value.getIdent(), value);
	}

	public LessVariableDeclarationNode getVariable(String name) {
		if (variables == null || !variables.containsKey(name)) {
			return null;
		}

		return variables.get(name);
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

		for (int i=0; i<this.children.size(); i++) {
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
		for (CssNode child : children) {
			if (child == null) {
				continue;
			}
			hash = hash * HASH_PRIME + child.hashCode();
		}
		return hash;
	}
}
