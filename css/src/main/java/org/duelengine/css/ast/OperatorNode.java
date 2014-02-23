package org.duelengine.css.ast;

/**
 * Represents an operator
 */
public class OperatorNode extends ValueNode {

	public OperatorNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public OperatorNode(String value) {
		super(value);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.OPERATOR;
	}

	@Override
	public WordBreak getWordBreak(boolean prettyPrint) {
		String value = this.getValue(!prettyPrint);
		if (value != null) {
			char ch = value.charAt(0);
			switch (ch) {
				case ',':
					return prettyPrint ? WordBreak.POST : WordBreak.NONE;
				case '(':
				case '[':
					return WordBreak.PRE;
				case ')':
				case ']':
					return WordBreak.POST;
			}
		}
		return WordBreak.NONE;
	}
}
