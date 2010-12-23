package org.cssless.css.ast;

import org.cssless.css.parsing.InvalidNodeException;

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

	public final void setIdent(String value) {
		this.ident = value;
	}

	public final String getIdent() {
		return this.ident;
	}

	public boolean isImportant() {
		return this.important;
	}

	public void setImportant(boolean value) {
		this.important = value;
	}

	public DeclarationNode withImportant() {
		this.important = true;
		return this;
	}

	@Override
	public void appendChild(CssNode value) {
		if (!(value instanceof ValueNode)) {
			throw new InvalidNodeException("Declaration may only hold values for its expression", value);
		}

		super.appendChild(value);
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

		int hash = super.hashCode() * HASH_PRIME + ((Boolean)this.important).hashCode();
		if (this.ident != null) {
			hash = hash * HASH_PRIME + this.ident.hashCode();
		}
		return hash;
	}
}
