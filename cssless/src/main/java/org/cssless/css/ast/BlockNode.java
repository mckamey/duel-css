package org.cssless.css.ast;

/**
 * Represents a block scope
 */
public class BlockNode extends ContainerNode {

	public BlockNode(int index, int line, int column) {
		super(index, line, column);
	}

	public BlockNode(CssNode... children) {
		super(children);
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.BLOCK;
	}
}
