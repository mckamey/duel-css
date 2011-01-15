package org.cssless.css.ast;

import org.cssless.css.parsing.InvalidNodeException;

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

	public String getKeyword() {
		return this.keyword;
	}

	public void setKeyword(String value) {
		this.keyword = value;
	}

	public BlockNode getBlock() {
		return this.block;
	}

	public void setBlock(BlockNode value) {
		this.block = value;
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
