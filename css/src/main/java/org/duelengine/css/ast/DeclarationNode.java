package org.duelengine.css.ast;

import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Represents a value
 */
public class DeclarationNode extends ContainerNode {

	private String ident;
	private boolean important;

	public DeclarationNode(String ident, int index, int line, int column) {
		super(index, line, column);

		this.ident = ident;
	}

	public DeclarationNode(String ident, ValueNode... expression) {
		super(expression);

		this.ident = ident;
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.DECLARATION;
	}

	public final void setIdent(String value) {
		ident = value;
	}

	public final String getIdent() {
		return ident;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean value) {
		important = value;
	}

	public DeclarationNode withImportant() {
		important = true;
		return this;
	}

	@Override
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof ValueNode) {
			return child;
		}

		throw new InvalidNodeException("Declaration may only hold values for its expression", child);
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof DeclarationNode)) {
			// includes null
			return false;
		}

		DeclarationNode that = (DeclarationNode)arg;
		if (this.ident == null ? that.ident != null : !this.ident.equals(that.ident)) {
			return false;
		}
		if (this.important != that.important) {
			return false;
		}

		return super.equals(arg);
	}

	@Override
	public int hashCode() {
		final int HASH_PRIME = 1000003;

		int hash = super.hashCode() * HASH_PRIME + ((Boolean)important).hashCode();
		if (ident != null) {
			hash = hash * HASH_PRIME + ident.hashCode();
		}
		return hash;
	}
}
