package org.cssless.css.ast;

/**
 * Represents a numeric value
 */
public class NumericNode extends ValueNode {

	private double number;
	private String units;

	public NumericNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public NumericNode(double number, String units, int index, int line, int column) {
		super(null, index, line, column);

		this.number = number;
		this.units = units;
		super.setValue(this.formatNumber(this.number)+this.units);
	}

	public NumericNode(String value) {
		super(value);
	}

	public NumericNode(double number, String units) {
		super(null);

		this.number = number;
		this.units = units;
		super.setValue(this.formatNumber(this.number)+this.units);
	}

	public double getNumber() {
		return this.number;
	}

	public void setNumber(double value) {
		this.number = value;

		super.setValue(this.formatNumber(this.number)+this.units);
	}

	public String getUnits() {
		return this.units;
	}

	public void setUnits(String value) {
		this.units = value;

		super.setValue(this.formatNumber(this.number)+this.units);
	}

	@Override
	public String getValue(boolean compact) {
		if (compact) {
			if (this.number == 0.0) {
				// no units needed for zero
				return "0";
			}

			return this.formatNumber(this.number)+this.units;
		}

		return super.getValue(compact);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);

		if (value == null || value.isEmpty()) {
			this.number = 0.0;
			this.units = null;
			return;
		}

		int index = value.length()-1;
		for (; index>=0; index--) {
			char ch = value.charAt(index);
			if (((ch >= '0') && (ch <= '9')) || ch == '.') {
				break;
			}
		}
		this.units = value.substring(index+1);

		try {
			this.number = Double.parseDouble(this.units.isEmpty() ? value : value.substring(0, index+1));
		} catch (NumberFormatException ex) {
			this.number = 0.0;
		}
	}

	@Override
	public ValueNode add(ValueNode operand) {
		while (operand instanceof LessNode) {
			operand = ((LessNode)operand).eval(this.getParent());
		}
		
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			String units = this.units;
			if (units == null || units.isEmpty()) {
				units = that.units;
			}
			return new NumericNode(this.number + that.number, units, this.getIndex(), this.getLine(), this.getColumn());
		}

		if (operand instanceof ColorNode) {
			// leverage commutative nature of addition
			return operand.add(this);
		}

		return super.add(operand);
	}

	@Override
	public ValueNode subtract(ValueNode operand) {
		while (operand instanceof LessNode) {
			operand = ((LessNode)operand).eval(this.getParent());
		}
		
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			String units = this.units;
			if (units == null || units.isEmpty()) {
				units = that.units;
			}
			return new NumericNode(this.number - that.number, units, this.getIndex(), this.getLine(), this.getColumn());
		}

		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = (int)(this.number - that.getRedChannel());
			int g = (int)(this.number - that.getGreenChannel());
			int b = (int)(this.number - that.getBlueChannel());
			return new ColorNode(r, g, b, this.getIndex(), this.getLine(), this.getColumn());
		}

		return super.subtract(operand);
	}

	@Override
	public ValueNode multiply(ValueNode operand) {
		while (operand instanceof LessNode) {
			operand = ((LessNode)operand).eval(this.getParent());
		}
		
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			String units = this.units;
			if (units == null || units.isEmpty()) {
				units = that.units;
			}
			return new NumericNode(this.number * that.number, units, this.getIndex(), this.getLine(), this.getColumn());
		}

		if (operand instanceof ColorNode) {
			// leverage commutative nature of multiplication
			return operand.multiply(this);
		}

		return super.multiply(operand);
	}

	@Override
	public ValueNode divide(ValueNode operand) {
		while (operand instanceof LessNode) {
			operand = ((LessNode)operand).eval(this.getParent());
		}
		
		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			String units = this.units;
			if (units == null || units.isEmpty()) {
				units = that.units;
			}
			return new NumericNode(this.number / that.number, units, this.getIndex(), this.getLine(), this.getColumn());
		}

		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = (int)(this.number - that.getRedChannel());
			int g = (int)(this.number - that.getGreenChannel());
			int b = (int)(this.number - that.getBlueChannel());
			return new ColorNode(r, g, b, this.getIndex(), this.getLine(), this.getColumn());
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
		if (this.units == null ? that.units != null : !this.units.equals(that.units)) {
			return false;
		}

		return (this.number == that.number);
	}
}
