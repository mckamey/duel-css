package org.cssless.css.codegen;

import static org.junit.Assert.*;
import java.io.IOException;
import org.cssless.css.ast.*;
import org.junit.Test;

public class ArithmeticEvaluatorTests {

	@Test
	public void evalPrecendenceTest() throws IOException {

		CssNode[] input = {
			new NumericNode("2"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode("-"),
			new NumericNode("5"),
			new OperatorNode("/"),
			new NumericNode("4")
		};

		ValueNode expected = new NumericNode("4.75");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalLeadingParensTest() throws IOException {

		CssNode[] input = {
			new OperatorNode("("),
			new NumericNode("8"),
			new OperatorNode("+"),
			new NumericNode("5"),
			new OperatorNode(")"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode("+"),
			new NumericNode("5"),
			new OperatorNode("*"),
			new NumericNode("6"),
			new OperatorNode("-"),
			new NumericNode("7")
		};

		ValueNode expected = new NumericNode("62");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalTrailingParensTest() throws IOException {

		CssNode[] input = {
			new NumericNode("3"),
			new OperatorNode("+"),
			new NumericNode("7"),
			new OperatorNode("/"),
			new OperatorNode("("),
			new NumericNode("4"),
			new OperatorNode("*"),
			new NumericNode("5"),
			new OperatorNode("-"),
			new NumericNode("6"),
			new OperatorNode(")")
		};

		ValueNode expected = new NumericNode("3.5");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalNestedParensTest() throws IOException {

		CssNode[] input = {
			new OperatorNode("("),
			new OperatorNode("("),
			new NumericNode("13"),
			new OperatorNode("-"),
			new OperatorNode("("),
			new NumericNode("4"),
			new OperatorNode("+"),
			new NumericNode("5"),
			new OperatorNode(")"),
			new OperatorNode(")"),
			new OperatorNode("*"),
			new OperatorNode("("),
			new OperatorNode("("),
			new NumericNode("3"),
			new OperatorNode("-"),
			new NumericNode("4"),
			new OperatorNode(")"),
			new OperatorNode("-"),
			new OperatorNode("("),
			new NumericNode("3"),
			new OperatorNode("+"),
			new NumericNode("5"),
			new OperatorNode(")"),
			new OperatorNode(")"),
			new OperatorNode(")"),
			new OperatorNode("/"),
			new NumericNode("3")
		};

		ValueNode expected = new NumericNode("-12");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}
}
