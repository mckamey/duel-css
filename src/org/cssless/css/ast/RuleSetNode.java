package org.cssless.css.ast;

import java.util.*;

/**
 * Represents a rule set block
 */
public class RuleSetNode extends BlockNode {

	private final List<SelectorNode> selectors = new ArrayList<SelectorNode>();

	public RuleSetNode(int index, int line, int column) {
		super(index, line, column);
	}

	public RuleSetNode(SelectorNode selector, CssNode... children) {
		super(children);

		if (selector != null) {
			this.selectors.add(selector);
		}
	}

	public RuleSetNode(SelectorNode[] selectors, CssNode... children) {
		super(children);

		if (selectors != null) {
			for (SelectorNode selector : selectors) {
				this.selectors.add(selector);
			}
		}
	}

	public List<SelectorNode> getSelectors() {
		return selectors;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		boolean needsDelim = false;
		for (SelectorNode selector : this.selectors) {
			if (needsDelim) {
				buffer.append(", ");
			} else {
				needsDelim = true;
			}
			buffer.append(selector.toString());
		}

		buffer.append(super.toString());
		
		return buffer.toString();
	}
}
