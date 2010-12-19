package org.cssless.css.ast;

import org.cssless.css.parsing.InvalidNodeException;

/**
 * Represents an rule statement/block
 */
public class AtRuleNode extends ContainerNode {

	private BlockNode block;
	private String keyword;

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
	public void appendChild(CssNode value) {
		if (!(value instanceof ValueNode)) {
			throw new InvalidNodeException("At-rule may only hold value nodes outside of block", value);
		}

		super.appendChild(value);
	}
}
