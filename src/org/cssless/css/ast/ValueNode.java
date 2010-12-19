package org.cssless.css.ast;

/**
 * Represents a value
 */
public class ValueNode extends CssNode {

	private String value;

	public ValueNode(String value, int index, int line, int column) {
		super(index, line, column);

		this.value = value;
	}

	public ValueNode(String value) {
		this.value = value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof ValueNode)) {
			// includes null
			return false;
		}

		ValueNode that = (ValueNode)arg;
		return (this.value == null ? that.value == null : this.value.equals(that.value));
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (this.value != null) {
			hash = this.value.hashCode();
		}
		return hash;
	}
}
