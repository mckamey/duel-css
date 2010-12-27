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
			// evaluate remaining operators
			ValueNode result = this.evalOp(operators.pop(), operands);
			if (result != null) {
				operands.push(result);
			}
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
			ValueNode result = this.evalOp(op, operands);
			if (result != null) {
				operands.push(result);
			}
		}
	}

	private ValueNode evalOp(OperatorNode op, Stack<ValueNode> operands) {

		String operator = op.getValue();
		if (operator != null && operator.length() == 1) {
			char opCh = operator.charAt(0);
			switch (opCh) {
				case '(':
				case ')':
					return null;
				case '+':
				case '-':
				case '*':
				case '/':
					// pop order is very important
					ValueNode right = operands.pop();
					ValueNode left = operands.pop();

					switch (opCh) {
						case '+':
							return left.add(right);

						case '-':
							return left.subtract(right);

						case '*':
							return left.multiply(right);

						case '/':
							return left.divide(right);
					}
					break;
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
