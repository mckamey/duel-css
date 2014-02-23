package org.duelengine.css.ast;

import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Represents an rule statement/block
 */
public class AtRuleNode extends ContainerNode {

	private String keyword;
	private BlockNode block;

	public AtRuleNode(String keyword, int index, int line, int column) {
		super(index, line, column);

		this.keyword = keyword;
	}

	public AtRuleNode(String keyword, ValueNode... values) {
		super(values);

		this.keyword = keyword;
	}

	public AtRuleNode(String keyword, ValueNode[] values, BlockNode block) {
		super(values);

		this.keyword = keyword;
		this.block = block;
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.AT_RULE;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String value) {
		keyword = value;
	}

	public BlockNode getBlock() {
		return block;
	}

	public void setBlock(BlockNode value) {
		block = value;
	}

	@Override
	protected CssNode filterChild(CssNode child) {
		child = super.filterChild(child);

		if (child == null || child instanceof ValueNode) {
			return child;
		}

		throw new InvalidNodeException("At-rule may only hold value nodes outside of block", child);
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof AtRuleNode)) {
			// includes null
			return false;
		}

		AtRuleNode that = (AtRuleNode)arg;
		if (this.keyword == null ? that.keyword != null : !this.keyword.equals(that.keyword)) {
			return false;
		}
		if (this.block == null ? that.block != null : !this.block.equals(that.block)) {
			return false;
		}

		return super.equals(arg);
	}
}
