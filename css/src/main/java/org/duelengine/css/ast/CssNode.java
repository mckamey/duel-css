package org.duelengine.css.ast;

import org.duelengine.css.codegen.CssFormatter;

public abstract class CssNode {

	private final int index;
	private final int line;
	private final int column;
	private ContainerNode parent;

	protected CssNode() {
		index = line = column = -1;
	}

	protected CssNode(int nodeIndex, int nodeLine, int nodeColumn) {
		index = nodeIndex;
		line = nodeLine;
		column = nodeColumn;
	}

	public abstract CssNodeType getNodeType();
	
	public int getIndex() {
		return index;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public WordBreak getWordBreak(boolean prettyPrint) {
		return WordBreak.NONE;
	}
	
	public ContainerNode getParent() {
		return parent;
	}

	void setParent(ContainerNode value) {
		parent = value;
	}

	/**
	 * Evaluates LESS node producing static content and modifying metadata
	 * May return null if does not generate any content
	 * @param context
	 * @return
	 */
	CssNode eval(ContainerNode context) {
		return this;
	}

	@Override
	public String toString() {
		try {
			StringBuilder buffer = new StringBuilder();
			new CssFormatter().writeNode(buffer, this, null);
			return buffer.toString();
		} catch (Exception ex) {
			return super.toString()+'\n'+ex.getMessage();
		}
	}
}
