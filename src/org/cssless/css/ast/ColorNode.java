package org.cssless.css.ast;

import org.cssless.css.parsing.CssGrammar;

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

	public ColorNode(String value) {
		super(value);
	}

	public int getRedChannel() {
		return this.red;
	}

	public int getGreenChannel() {
		return this.green;
	}

	public int getBlueChannel() {
		return this.blue;
	}

	public void setChannels(int r, int g, int b) {
		this.hasChannels = true;
		this.red = r;
		this.green = g;
		this.blue = b;

		// hex notation
		super.setValue("#" + toHex(r) + toHex(g) + toHex(b));
	}
	
	@Override
	public String getValue(boolean compact) {
		String color = super.getValue(compact);
		if (compact && this.hasChannels &&
			(color == null || color.isEmpty() || color.length() > 4)) {

			String r = toHex(this.red);
			String g = toHex(this.green);
			String b = toHex(this.blue);

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
			this.hasChannels = true;
			this.red = 0;
			this.green = 0;
			this.blue = 0;
			return;
		}

		String color = CssGrammar.decodeColor(value);
		if (color == null || color.isEmpty()) {
			color = value;
		}

		int length = color.length();
		if ((length != 4 && length != 7) || color.charAt(0) != '#') {
			this.hasChannels = false;
			this.red = 0;
			this.green = 0;
			this.blue = 0;
			return;
		}

		this.hasChannels = true;
		if (length == 4) {
			this.red = fromHex(color.substring(1, 2)+color.substring(1, 2));
			this.green = fromHex(color.substring(2, 3)+color.substring(2, 3));
			this.blue = fromHex(color.substring(3, 4)+color.substring(3, 4));
		} else {
			this.red = fromHex(color.substring(1, 3));
			this.green = fromHex(color.substring(3, 5));
			this.blue = fromHex(color.substring(5, 7));
		}
	}

	private static int fromHex(String channel) {
		return Integer.parseInt(channel, 16);
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
}
