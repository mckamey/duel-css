package org.duelengine.css.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.duelengine.css.parsing.InvalidNodeException;

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
			addSelector(selector);
		}
	}

	public RuleSetNode(SelectorNode[] selectors, CssNode... children) {
		super(children);

		if (selectors != null) {
			for (SelectorNode selector : selectors) {
				addSelector(selector);
			}
		}
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.RULE_SET;
	}

	public Collection<SelectorNode> getSelectors() {
		return selectors;
	}

	@Override
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof DeclarationNode || child instanceof CommentNode) {
			return child;
		}

		if (child instanceof RuleSetNode) {
			// LESS allows nested rules, trickle up to parent
			getParent().appendChild(child);
			return null;
		}

		throw new InvalidNodeException("Rule-sets may only directly hold declarations and comments", child);
	}

	public void expandSelectors(Collection<SelectorNode> prefixes) {
		List<SelectorNode> expanded = new ArrayList<SelectorNode>(prefixes.size() * selectors.size());

		for (SelectorNode suffix : selectors) {
			ValueNode first = ((ValueNode)suffix.getFirstChild());
			boolean mergedSelectors = (first.getValue() != null) && first.getValue().startsWith("&");

			for (SelectorNode prefix : prefixes) {
				SelectorNode selector = new SelectorNode();
				expanded.add(selector);

				List<CssNode> parts = selector.getChildren(); 
				parts.addAll(prefix.getChildren());
				if (!mergedSelectors) {
					parts.addAll(suffix.getChildren());
					continue;
				}

				// merge pseudo-classes
				String last = ((ValueNode)parts.get(parts.size()-1)).getValue();
				parts.set(parts.size()-1, new ValueNode(last+first.getValue().substring(1), first.getIndex(), first.getLine(), first.getColumn()));
				for (int i=1, length=suffix.childCount(); i<length; i++) {
					parts.add(suffix.getChildren().get(i));
				}
			}
		}

		selectors.clear();
		for (SelectorNode selector : expanded) {
			addSelector(selector);
		}
	}

	public void addSelector(SelectorNode selector) {
		if (selector == null) {
			return;
		}

		selectors.add(selector);
		selector.setParent(this);
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
