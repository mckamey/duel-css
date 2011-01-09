package org.cssless.css.ast;

public class FunctionNode extends ContainerValueNode {

	public FunctionNode(String value, int index, int line, int column) {
		super(value, index, line, column);
	}

	public FunctionNode(String value, ValueNode... args) {
		super(value, args);
	}

	@Override
	public WordBreak getWordBreak(boolean prettyPrint) {
		return WordBreak.BOTH;
	}
}
