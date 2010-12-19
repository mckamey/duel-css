package org.cssless.css.ast;

import org.cssless.css.parsing.InvalidNodeException;

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
	public void appendChild(CssNode child) {
		if (!(child instanceof RuleSetNode || child instanceof AtRuleNode || child instanceof CommentNode)) {
			throw new InvalidNodeException("StyleSheets may only directly hold at-rules, rule-sets and comments", child);
		}
		super.appendChild(child);
	}
}
