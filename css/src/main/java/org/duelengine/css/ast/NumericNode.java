package org.duelengine.css.ast;

import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Represents a numeric value
 */
public class NumericNode extends ValueNode {

	private double number;
	private String units;
	private boolean keepUnits;

	public NumericNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public NumericNode(double number, String units, int index, int line, int column) {
		super(null, index, line, column);

		this.number = number;
		this.units = units;
		super.setValue(formatNumber(number)+units);
	}

	public NumericNode(String value) {
		super(value);
	}

	public NumericNode(double number, String units) {
		super(null);

		this.number = number;
		this.units = units;
		super.setValue(formatNumber(number)+units);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.NUMERIC;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double value) {
		number = value;

		super.setValue(formatNumber(number)+units);
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String value) {
		units = value;

		super.setValue(formatNumber(number)+units);
	}

	public boolean getKeepUnits() {
		return keepUnits;
	}

	public void setKeepUnits(boolean value) {
		keepUnits = value;
	}

	@Override
	public String getValue(boolean compact) {
		if (compact) {
			if (!keepUnits && number == 0.0) {
				// no units needed for zero
				return "0";
			}

			return formatNumber(number)+units;
		}

		return super.getValue(compact);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);

		if (value == null || value.isEmpty()) {
			number = 0.0;
			units = null;
			return;
		}

		int index = value.length()-1;
		for (; index>=0; index--) {
			char ch = value.charAt(index);
			if (((ch >= '0') && (ch <= '9')) || ch == '.') {
				break;
			}
		}

		units = value.substring(index+1);

		try {
			number = Double.parseDouble(units.isEmpty() ? value : value.substring(0, index+1));

		} catch (NumberFormatException ex) {
			number = 0.0;
			units = null;
		}
	}

	@Override
	public ValueNode add(ValueNode operand) {
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (this.units == null || this.units.isEmpty()) {
				this.units = that.units;
			} else if (that.units != null && !that.units.isEmpty() && !this.units.equals(that.units)) {
				throw new InvalidNodeException("Incompatible units: "+this+", "+that, that);
			}
			return new NumericNode(this.number + that.number, units, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof ColorNode) {
			// leverage commutative nature of addition
			return operand.add(this);
		}

		return super.add(operand);
	}

	@Override
	public ValueNode subtract(ValueNode operand) {
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (this.units == null || this.units.isEmpty()) {
				this.units = that.units;
			} else if (that.units != null && !that.units.isEmpty() && !this.units.equals(that.units)) {
				throw new InvalidNodeException("Incompatible units: "+this+", "+that, that);
			}
			return new NumericNode(this.number - that.number, this.units, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof ColorNode) {
			if (units != null && !units.isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+this, this);
			}
			ColorNode that = (ColorNode)operand;
			int r = (int)(number - that.getRedChannel());
			int g = (int)(number - that.getGreenChannel());
			int b = (int)(number - that.getBlueChannel());
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.subtract(operand);
	}

	@Override
	public ValueNode multiply(ValueNode operand) {
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (this.units == null || this.units.isEmpty()) {
				this.units = that.units;
			} else if (that.units != null && !that.units.isEmpty() && !this.units.equals(that.units)) {
				throw new InvalidNodeException("Incompatible units: "+this+", "+that, that);
			}
			return new NumericNode(this.number * that.number, units, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof ColorNode) {
			// leverage commutative nature of multiplication
			return operand.multiply(this);
		}

		return super.multiply(operand);
	}

	@Override
	public ValueNode divide(ValueNode operand) {
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (this.units == null || this.units.isEmpty()) {
				this.units = that.units;
			} else if (that.units != null && !that.units.isEmpty() && !this.units.equals(that.units)) {
				throw new InvalidNodeException("Incompatible units: "+this+", "+that, that);
			}
			return new NumericNode(this.number / that.number, this.units, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof ColorNode) {
			if (units != null && !units.isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+this, this);
			}
			ColorNode that = (ColorNode)operand;
			int r = (int)(number - that.getRedChannel());
			int g = (int)(number - that.getGreenChannel());
			int b = (int)(number - that.getBlueChannel());
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.divide(operand);
	}

	private String formatNumber(double value) {
		if (value == (double)((long)value)) {
			// integers should be formatted without trailing decimals
			return Long.toString((long)value);

		} else {
			// NOTE: prints NaN, Infinity, -Infinity too
			return Double.toString(value);
		}
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof NumericNode) || !this.getClass().equals(arg.getClass())) {
			// includes null
			return false;
		}

		NumericNode that = (NumericNode)arg;
		if (this.keepUnits != that.keepUnits) {
			return false;
		}
		if (this.units == null ? that.units != null : !this.units.equals(that.units)) {
			return false;
		}

		return (this.number == that.number);
	}
}
