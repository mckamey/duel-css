package org.cssless.css.ast;

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
}
