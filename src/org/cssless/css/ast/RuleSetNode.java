package org.cssless.css.ast;

import java.util.*;

import org.cssless.css.parsing.InvalidNodeException;

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
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof DeclarationNode || child instanceof CommentNode) {
			return child;
		}

		throw new InvalidNodeException("Rule-sets may only directly hold declarations and comments", child);
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof RuleSetNode)) {
			// includes null
			return false;
		}

		RuleSetNode that = (RuleSetNode)arg;
		if (this.selectors.size() != that.selectors.size()) {
			return false;
		}

		for (int i=0, length=this.selectors.size(); i<length; i++) {
			CssNode a = this.selectors.get(i);
			CssNode b = that.selectors.get(i);
			if (a == null ? b != null : !a.equals(b)) {
				return false;
			}
		}

		return super.equals(arg);
	}
}
