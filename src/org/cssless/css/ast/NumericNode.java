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

	public NumericNode(String value) {
		super(value);
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

	private String formatNumber(double value) {
		if (value == (double)((long)value)) {
			// integers should be formatted without trailing decimals
			return Long.toString((long)value);

		} else {
			// NOTE: prints NaN, Infinity, -Infinity too
			return Double.toString(value);
		}
	}
}
