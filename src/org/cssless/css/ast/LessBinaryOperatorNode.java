package org.cssless.css.ast;

public class LessBinaryOperatorNode extends ValueNode implements LessNode {

	private LessBinaryOperatorType operator;
	private ValueNode left;
	private ValueNode right;
	
	public LessBinaryOperatorNode(int index, int line, int column) {
		super(null, index, line, column);
	}

	public LessBinaryOperatorNode(LessBinaryOperatorType operator, ValueNode left, ValueNode right) {
		super(null);
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public void setOperator(LessBinaryOperatorType value) {
		this.operator = value;
	}

	public LessBinaryOperatorType getOperator() {
		return this.operator;
	}

	public void setLeft(ValueNode value) {
		this.left = value;
	}

	public ValueNode getLeft() {
		return this.left;
	}

	public void setRight(ValueNode value) {
		this.right = value;
	}

	public ValueNode getRight() {
		return this.right;
	}

	private String formatOperator(boolean compact) {
		switch (this.operator) {
			case ADD:
				return compact ? "%1$s+%2$s" : "%1$s + %2$s";

			case SUBTRACT:
				return compact ? "%1$s-%2$s" : "%1$s - %2$s";

			case MULTIPLY:
				return compact ? "%1$s*%2$s" : "%1$s * %2$s";

			case DIVIDE:
				return compact ? "%1$s/%2$s" : "%1$s / %2$s";

			case ACCESSOR:
				return "%1$s[%2$s]";

			case CHILD:
				return compact ? "%1$s>%2$s" : "%1$s > %2$s";

			case DESCENDANT:
			default:
				return "%1$s %2$s";
		}
	}

	@Override
	public String getValue(boolean compact) {
		String format = this.formatOperator(compact);
		String leftStr = (this.left != null) ? this.left.toString() : "0";
		String rightStr = (this.right != null) ? this.right.toString() : "0";
		return String.format(format, leftStr, rightStr);
	}

	@Override
	public void setValue(String value) {
		// ignore
	}

	public ValueNode eval(ContainerNode context) {
		if (this.operator == null) {
			throw new NullPointerException("operator");
		}
		if (this.left == null) {
			throw new NullPointerException("left");
		}
		if (this.right == null) {
			throw new NullPointerException("right");
		}
		
		switch (this.operator) {
			case ADD:
				return this.left.add(this.right);

			case SUBTRACT:
				return this.left.subtract(this.right);

			case MULTIPLY:
				return this.left.multiply(this.right);

			case DIVIDE:
				return this.left.divide(this.right);

			case ACCESSOR:
				// TODO: find left in context, find right declaration in left's ruleset
				throw new UnsupportedOperationException("Operator not yet implemented: "+this.operator);

			case CHILD:
				// TODO: find left in context, find right in left's direct children
				throw new UnsupportedOperationException("Operator not yet implemented: "+this.operator);

			case DESCENDANT:
				// TODO: find left in context, find right in left's descendants
				throw new UnsupportedOperationException("Operator not yet implemented: "+this.operator);
				
			default:
				throw new IllegalStateException("Invalid operator: "+this.operator);
		}
	}

	@Override
	public ValueNode add(ValueNode operand) {
		return this.eval(this.getParent()).add(operand);
	}

	@Override
	public ValueNode subtract(ValueNode operand) {
		return this.eval(this.getParent()).subtract(operand);
	}

	@Override
	public ValueNode multiply(ValueNode operand) {
		return this.eval(this.getParent()).multiply(operand);
	}

	@Override
	public ValueNode divide(ValueNode operand) {
		return this.eval(this.getParent()).divide(operand);
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof LessBinaryOperatorNode)) {
			// includes null
			return false;
		}

		LessBinaryOperatorNode that = (LessBinaryOperatorNode)arg;
		if (this.operator == null ? that.operator != null : !this.operator.equals(that.operator)) {
			return false;
		}
		if (this.left == null ? that.left != null : !this.left.equals(that.left)) {
			return false;
		}
		if (this.right == null ? that.right != null : !this.right.equals(that.right)) {
			return false;
		}

		return true;
	}
}
