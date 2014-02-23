package org.duelengine.css.ast;

/**
 * Represents an inline comment
 */
public class CommentNode extends ValueNode {

	public CommentNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public CommentNode(String value) {
		super(value);
	}

	@Override
	public WordBreak getWordBreak(boolean prettyPrint) {
		return prettyPrint ? WordBreak.BOTH : WordBreak.NONE;
	};

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.COMMENT;
	}
}
