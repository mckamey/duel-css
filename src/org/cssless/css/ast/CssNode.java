package org.cssless.css.ast;

public abstract class CssNode {

	private final int index;
	private final int line;
	private final int column;
	private ContainerNode parent;

	protected CssNode() {
		this.index = -1;
		this.line = -1;
		this.column = -1;
	}

	protected CssNode(int index, int line, int column) {
		this.index = index;
		this.line = line;
		this.column = column;
	}

	public int getIndex() {
		return index;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public ContainerNode getParent() {
		return this.parent;
	}

	void setParent(ContainerNode parent) {
		this.parent = parent;
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
			new org.cssless.css.codegen.CssFormatter().writeNode(buffer, this);
			return buffer.toString();
		} catch (Exception ex) {
			return super.toString()+'\n'+ex.getMessage();
		}
	}
}
