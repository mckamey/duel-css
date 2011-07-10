package org.cssless.css.ast;

import org.cssless.css.parsing.InvalidNodeException;

/**
 * Represents a CSS3 selector
 * http://www.w3.org/TR/css3-selectors/
 * http://www.w3.org/TR/CSS/selector.html#selector-syntax
 */
public class SelectorNode extends ContainerNode {

	public SelectorNode(int index, int line, int column) {
		super(index, line, column);
	}

	public SelectorNode(ValueNode... sequence) {
		super(sequence);
	}

	public SelectorNode(String value) {
		super(new ValueNode(value));
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.SELECTOR;
	}

	@Override
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof ValueNode) {
			return child;
		}

		throw new InvalidNodeException("Selector may only hold value nodes", child);
	}
}
