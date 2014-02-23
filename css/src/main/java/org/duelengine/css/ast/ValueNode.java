package org.duelengine.css.ast;

/**
 * Represents a value
 */
public class ValueNode extends CssNode {

	private String value;

	public ValueNode(String value, int index, int line, int column) {
		super(index, line, column);

		this.setValue(value);
	}

	public ValueNode(String value) {
		this.setValue(value);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.VALUE;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.getValue(false);
	}

	public String getValue(boolean compact) {
		return this.value;
	}

	public ValueNode add(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(this.getClass().getName()+" does not support addition with "+operand.getClass().getName());
	}

	public ValueNode subtract(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(this.getClass().getName()+" does not support subtraction with "+operand.getClass().getName());
	}

	public ValueNode multiply(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(this.getClass().getName()+" does not support multiplication with "+operand.getClass().getName());
	}

	public ValueNode divide(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(this.getClass().getName()+" does not support division with "+operand.getClass().getName());
	}

	@Override
	public WordBreak getWordBreak(boolean prettyPrint) {
		return WordBreak.BOTH;
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof ValueNode) || !this.getClass().equals(arg.getClass())) {
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
