package org.duelengine.css.ast;

/**
 * Represents a value
 */
public class ValueNode extends CssNode {

	private String nodeValue;

	public ValueNode(String value, int index, int line, int column) {
		super(index, line, column);

		setValue(value);
	}

	public ValueNode(String value) {
		setValue(value);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.VALUE;
	}

	public void setValue(String value) {
		nodeValue = value;
	}

	public String getValue() {
		return getValue(false);
	}

	public String getValue(boolean compact) {
		return nodeValue;
	}

	public ValueNode add(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(getClass().getName()+" does not support addition with "+operand.getClass().getName());
	}

	public ValueNode subtract(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(getClass().getName()+" does not support subtraction with "+operand.getClass().getName());
	}

	public ValueNode multiply(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(getClass().getName()+" does not support multiplication with "+operand.getClass().getName());
	}

	public ValueNode divide(ValueNode operand) {
		if (operand == null) {
			throw new NullPointerException("operand");
		}
		throw new UnsupportedOperationException(getClass().getName()+" does not support division with "+operand.getClass().getName());
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
		return (this.nodeValue == null ? that.nodeValue == null : this.nodeValue.equals(that.nodeValue));
	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (nodeValue != null) {
			hash = nodeValue.hashCode();
		}
		return hash;
	}
}
