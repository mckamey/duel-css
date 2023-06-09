package org.duelengine.css.ast;

import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Represents the style sheet root
 */
public class StyleSheetNode extends ContainerNode {

	public StyleSheetNode(int index, int line, int column) {
		super(index, line, column);
	}

	public StyleSheetNode(CssNode... children) {
		super(children);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.STYLESHEET;
	}

	@Override
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof RuleSetNode || child instanceof AtRuleNode || child instanceof CommentNode) {
			return child;
		}

		throw new InvalidNodeException("StyleSheets may only directly hold at-rules, rule-sets and comments", child);
	}
}
