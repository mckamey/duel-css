package org.duelengine.css.codegen;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

import org.duelengine.css.ast.ContainerNode;
import org.duelengine.css.ast.CssNode;
import org.duelengine.css.ast.MultiValueNode;
import org.duelengine.css.ast.OperatorNode;
import org.duelengine.css.ast.ValueNode;
import org.duelengine.css.parsing.InvalidNodeException;

/**
 * Implements single-pass variant of Dijkstra's shunting-yard algorithm.
 * Inherently thread-safe as contains no mutable instance data.
 */
public final class ArithmeticEvaluator {

	/**
	 * Evaluates sequences of values and operators as arithmetic expressions
	 */
	public static ValueNode eval(CssNode... expr) {
		return eval(Arrays.asList(expr));
	}

	/**
	 * Evaluates sequences of values and operators as arithmetic expressions
	 * @param expr
	 * @return
	 */
	public static ValueNode eval(Iterable<CssNode> expr) {
		try {
			Stack<OperatorNode> operators = new Stack<OperatorNode>();
			Stack<ValueNode> operands = new Stack<ValueNode>();
	
			boolean lastWasVar = false;
			for (CssNode next : expr) {
				if (next instanceof OperatorNode) {
					int nextPrecedence = precedence((OperatorNode)next);
					if (nextPrecedence < 0) {
						// unknown operator signals start of new expression
						flushOperators(operators, operands);
						// operator is treated as a delimiter
						operands.add((ValueNode)next);
	
					} else {
						if (lastWasVar && "(".equals(((OperatorNode)next).getValue())) {
							// var/parens boundary signals start of new expression
							flushOperators(operators, operands);
						}
	
						processOp(operators, operands, (OperatorNode)next);
					}
					lastWasVar = false;
	
				} else if (next instanceof ValueNode) {
					if (lastWasVar) {
						// two values without an infix operator signals start of new expression
						flushOperators(operators, operands);
					}
					operands.push((ValueNode)next);
					lastWasVar = true;
	
				} else {
					throw new InvalidNodeException("Unexpected expression node: "+next.getClass().getName(), next);
				}
			}

			flushOperators(operators, operands);
	
			int length = operands.size();
			switch (length) {
				case 0:
					return null;
				case 1:
					return operands.pop();
				default:
					ValueNode first = operands.get(0);
					MultiValueNode multi = new MultiValueNode(first.getIndex(), first.getLine(), first.getColumn());
					ContainerNode container = multi.getContainer();
					for (int i=0; i<length; i++) {
						container.appendChild(operands.get(i));
					}
					return multi;
			}

		} catch (InvalidNodeException ex) {
			throw ex;

		} catch (Exception ex) {
			Iterator<CssNode> iterator = expr.iterator();
			CssNode node = iterator.hasNext() ? iterator.next() : null;
			throw new InvalidNodeException(ex.getMessage(), node, ex);
		}
	}

	private static void flushOperators(Stack<OperatorNode> operators, Stack<ValueNode> operands) {
		// evaluate operators on stack
		while (!operators.isEmpty()) {
			ValueNode result = evalOp(operators.pop(), operands);
			if (result != null) {
				operands.push(result);
			}
		}
	}

	private static void processOp(Stack<OperatorNode> operators, Stack<ValueNode> operands, OperatorNode next) {
		while (true) {
			if (operators.isEmpty() || "(".equals(next.getValue()) ||
				precedence(next) > precedence(operators.peek())) {
				// push operator for later evaluation
				operators.push(next);
				return;
			}

			OperatorNode op = operators.pop();
			if ("(".equals(op.getValue()) && ")".equals(next.getValue())) {
				// consume matching parens
				return;
			}

			// eval top operator, push result on operand stack
			ValueNode result = evalOp(op, operands);
			if (result != null) {
				operands.push(result);
			}
		}
	}

	private static ValueNode evalOp(OperatorNode op, Stack<ValueNode> operands) {
		String operator = op.getValue();
		if (operator != null && operator.length() == 1) {
			char opCh = operator.charAt(0);
			switch (opCh) {
				case '(':
//				case ')':
					// consume
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

		// pass unknown operators through
		return op;
	}

	private static int precedence(OperatorNode node) {
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
