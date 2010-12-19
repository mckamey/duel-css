package org.cssless.css.ast;

/**
 * Represents a block scope
 */
public class BlockNode extends ContainerNode {

	private static final String BEGIN = "{"; 
	private static final String END = "}"; 

	public BlockNode(int index, int line, int column) {
		super(index, line, column);
	}

	public BlockNode(CssNode... children) {
		super(children);
	}

	@Override
	public String toString() {
		return BEGIN + super.toString() + END;
	}
}
