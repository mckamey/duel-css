package org.duelengine.css.ast;

import org.duelengine.css.parsing.CssGrammar;
import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Represents a color value
 */
public class ColorNode extends ValueNode {

	private boolean hasChannels;
	private int red;
	private int green;
	private int blue;

	public ColorNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public ColorNode(int r, int g, int b, int index, int line, int column) {
		super(null, index, line, column);

		setChannels(r, g, b);
	}

	public ColorNode(String value) {
		super(value);
	}

	public ColorNode(int r, int g, int b) {
		super(null);

		setChannels(r, g, b);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.COLOR;
	}

	public int getRedChannel() {
		return red;
	}

	public int getGreenChannel() {
		return green;
	}

	public int getBlueChannel() {
		return blue;
	}

	public void setChannels(int r, int g, int b) {
		// ensure channel bounds
		red = r = Math.max(0x00, Math.min(0xFF, r));
		green = g = Math.max(0x00, Math.min(0xFF, g));
		blue = b = Math.max(0x00, Math.min(0xFF, b));
		hasChannels = true;

		// hex notation
		super.setValue("#" + toHex(r) + toHex(g) + toHex(b));
	}
	
	@Override
	public String getValue(boolean compact) {
		String color = super.getValue(compact);
		if (compact && hasChannels &&
			(color == null || color.isEmpty() || color.length() > 4)) {

			String r = toHex(red);
			String g = toHex(green);
			String b = toHex(blue);

			if (r.charAt(0) == r.charAt(1) &&
				g.charAt(0) == g.charAt(1) &&
				b.charAt(0) == b.charAt(1)) {
				// shorthand hex notation
				return "#" + r.charAt(0) + g.charAt(0) + b.charAt(0);
			}

			if (color == null || color.isEmpty() || color.length() > 7) {
				// hex notation
				return "#" + r + g + b;
			}
		}
		return color;
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);

		if (value == null || value.isEmpty()) {
			hasChannels = true;
			red = 0;
			green = 0;
			blue = 0;
			return;
		}

		String color = CssGrammar.decodeColor(value);
		if (color == null || color.isEmpty()) {
			color = value;
		}

		int length = color.length();
		if ((length != 4 && length != 7) || color.charAt(0) != '#') {
			hasChannels = false;
			red = 0;
			green = 0;
			blue = 0;
			return;
		}

		hasChannels = true;
		if (length == 4) {
			red = fromHex(color.substring(1, 2)+color.substring(1, 2));
			green = fromHex(color.substring(2, 3)+color.substring(2, 3));
			blue = fromHex(color.substring(3, 4)+color.substring(3, 4));
		} else {
			red = fromHex(color.substring(1, 3));
			green = fromHex(color.substring(3, 5));
			blue = fromHex(color.substring(5, 7));
		}
	}

	@Override
	public ValueNode add(ValueNode operand) {
		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = this.red + that.red;
			int g = this.green + that.green;
			int b = this.blue + that.blue;
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (that.getUnits() != null && !that.getUnits().isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+that, that);
			}
			double number = that.getNumber();
			int r = (int)(red + number);
			int g = (int)(green + number);
			int b = (int)(blue + number);
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.add(operand);
	}

	@Override
	public ValueNode subtract(ValueNode operand) {
		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = this.red - that.red;
			int g = this.green - that.green;
			int b = this.blue - that.blue;
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (that.getUnits() != null && !that.getUnits().isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+that, that);
			}
			double number = that.getNumber();
			int r = (int)(red - number);
			int g = (int)(green - number);
			int b = (int)(blue - number);
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.subtract(operand);
	}

	@Override
	public ValueNode multiply(ValueNode operand) {
		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = this.red * that.red;
			int g = this.green * that.green;
			int b = this.blue * that.blue;
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (that.getUnits() != null && !that.getUnits().isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+that, that);
			}
			double number = that.getNumber();
			int r = (int)(red * number);
			int g = (int)(green * number);
			int b = (int)(blue * number);
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.multiply(operand);
	}

	@Override
	public ValueNode divide(ValueNode operand) {
		if (operand instanceof ColorNode) {
			ColorNode that = (ColorNode)operand;
			int r = this.red / that.red;
			int g = this.green / that.green;
			int b = this.blue / that.blue;
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		if (operand instanceof NumericNode) {
			NumericNode that = (NumericNode)operand;
			if (that.getUnits() != null && !that.getUnits().isEmpty()) {
				throw new InvalidNodeException("Cannot use units when mixing numeric and color: "+that, that);
			}
			double number = that.getNumber();
			int r = (int)(red / number);
			int g = (int)(green / number);
			int b = (int)(blue / number);
			return new ColorNode(r, g, b, getIndex(), getLine(), getColumn());
		}

		return super.divide(operand);
	}

	private static int fromHex(String channel) {
		// parse and ensure channel bounds
		return Math.max(0x00, Math.min(0xFF, Integer.parseInt(channel, 16)));
	}

	private static String toHex(int channel) {
		char[] hex = {
			getHexDigit(channel / 0x10),
			getHexDigit(channel % 0x10)
		};

		return String.valueOf(hex);
	}

	private static char getHexDigit(int digit) {
		if (digit < 10) {
			return (char)(digit + '0');
		}

		return (char)((digit - 10) + 'A');
	}
	
	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof ColorNode) || !this.getClass().equals(arg.getClass())) {
			// includes null
			return false;
		}

		ColorNode that = (ColorNode)arg;
		return
			(this.red == that.red) &&
			(this.green == that.green) &&
			(this.blue == that.blue) &&
			(this.hasChannels == that.hasChannels);
	}
}
