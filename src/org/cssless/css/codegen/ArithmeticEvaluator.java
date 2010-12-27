package org.cssless.css.codegen;

import java.util.*;
import org.cssless.css.ast.*;

/**
 * Implements single-pass variant of Dijkstra's shunting-yard algorithm.
 */
public class ArithmeticEvaluator {

	/**
	 * Evaluates sequences of values and operators as arithmetic expressions
	 */
	public ValueNode eval(CssNode... expr) {
		return this.eval(Arrays.asList(expr));
	}

	/**
	 * Evaluates sequences of values and operators as arithmetic expressions
	 * @param expr
	 * @return
	 */
	public ValueNode eval(Iterable<CssNode> expr) {
		Stack<OperatorNode> operators = new Stack<OperatorNode>();
		Stack<ValueNode> operands = new Stack<ValueNode>();

		for (CssNode next : expr) {
			if (next instanceof OperatorNode) {
				this.processOp(operators, operands, (OperatorNode)next);

			} else if (next instanceof ValueNode) {
				operands.push((ValueNode)next);

			} else {
				throw new IllegalStateException("Unexpected expression node: "+next.getClass().getName());
			}
		}

		while (!operators.isEmpty()) {
			OperatorNode op = operators.pop();
			String operator = op.getValue();
			if ("(".equals(operator) || ")".equals(operator)) {
				continue;
			}

			// evaluate remaining operators
			ValueNode right = operands.pop();
			ValueNode left = operands.pop();
			operands.push(this.evalOp(op, left, right));
		}

		if (operands.size() != 1) {
			throw new IllegalStateException("Eval ended with extra operands: "+operands.size());
		}

		return operands.pop();
	}

	private void processOp(Stack<OperatorNode> operators, Stack<ValueNode> operands, OperatorNode next) {
		while (true) {
			if (operators.isEmpty() ||
				"(".equals(next.getValue()) ||
				this.precedence(next) > this.precedence(operators.peek())) {
				// queue operator for later evaluation
				operators.push(next);
				return;
			}

			OperatorNode op = operators.pop();
			if ("(".equals(op.getValue()) && ")".equals(next.getValue())) {
				// consume matching parens
				return;
			}

			// eval top operator, push result on operand stack
			ValueNode right = operands.pop();
			ValueNode left = operands.pop();
			operands.push(this.evalOp(op, left, right));
		}
	}

	private ValueNode evalOp(OperatorNode op, ValueNode left, ValueNode right) {
		String operator = op.getValue();

		if (operator != null && operator.length() == 1) {
			switch (operator.charAt(0)) {
				case '+':
					return left.add(right);

				case '-':
					return left.subtract(right);

				case '*':
					return left.multiply(right);

				case '/':
					return left.divide(right);
			}
		}

		throw new IllegalStateException("Unknown operator: "+operator);
	}

	private int precedence(OperatorNode node) {
		String operator = node.getValue();

		if (operator != null && operator.length() == 1) {
			switch (operator.charAt(0)) {
				case '(':
				case ')':
					return 0;
				case '+':
				case '-':
					return 1;
				case '*':
				case '/':
//				case '%':
					return 2;
			}
		}

		// unknown
		return -1;
	}
}
