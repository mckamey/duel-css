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
	public void appendChild(CssNode value) {
		if (!(value instanceof ValueNode)) {
			throw new InvalidNodeException("Selector may only hold value nodes", value);
		}

		super.appendChild(value);
	}
}
