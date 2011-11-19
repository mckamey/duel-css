package org.cssless.css.codegen;

import static org.junit.Assert.*;
import org.cssless.css.ast.*;
import org.cssless.css.parsing.InvalidNodeException;
import org.junit.Test;

public class ArithmeticEvaluatorTest {

	@Test
	public void evalOperandSingleTest() {

		CssNode[] input = {
			new ColorNode("yellow")
		};

		ValueNode expected = new ColorNode("#FF0");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiOperandTest() {

		CssNode[] input = {
			new NumericNode("5px"),
			new NumericNode("10px"),
			new NumericNode("20px")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("5px"),
			new NumericNode("10px"),
			new NumericNode("20px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprTest() {

		CssNode[] input = {
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new NumericNode("2"),
			new OperatorNode("+"),
			new NumericNode("10px"),
			new NumericNode("19"),
			new OperatorNode("-"),
			new NumericNode("1px")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("15px"),
			new NumericNode("12px"),
			new NumericNode("18px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprCommaTest() {

		CssNode[] input = {
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode(","),
			new NumericNode("2"),
			new OperatorNode("+"),
			new NumericNode("10px"),
			new NumericNode("19"),
			new OperatorNode("-"),
			new NumericNode("1px")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("15px"),
			new OperatorNode(","),
			new NumericNode("12px"),
			new NumericNode("18px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprLeadingParensTest() {

		CssNode[] input = {
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode("("),
			new NumericNode("2"),
			new OperatorNode("*"),
			new NumericNode("10px"),
			new OperatorNode(")")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("15px"),
			new NumericNode("20px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprMiddleParensTest() {

		CssNode[] input = {
			new NumericNode("25%"),
			new OperatorNode("+"),
			new NumericNode("5"),
			new OperatorNode("("),
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode(")"),
			new NumericNode("2"),
			new OperatorNode("*"),
			new NumericNode("10px")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("30%"),
			new NumericNode("15px"),
			new NumericNode("20px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprTrailingParensTest() {

		CssNode[] input = {
			new OperatorNode("("),
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode(")"),
			new NumericNode("2"),
			new OperatorNode("*"),
			new NumericNode("10px")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("15px"),
			new NumericNode("20px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiExprBothParensTest() {

		CssNode[] input = {
			new OperatorNode("("),
			new NumericNode("5px"),
			new OperatorNode("*"),
			new NumericNode("3"),
			new OperatorNode(")"),
			new OperatorNode("("),
			new NumericNode("2"),
			new OperatorNode("*"),
			new NumericNode("10px"),
			new OperatorNode(")")
		};

		ValueNode expected = new MultiValueNode(
			new NumericNode("15px"),
			new NumericNode("20px"));

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalPrecendenceTest() {

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
	public void evalLeadingParensTest() {

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
	public void evalTrailingParensTest() {

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
	public void evalNestedParensTest() {

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

	@Test
	public void evalAddUnitsTest() {

		CssNode[] input = {
			new NumericNode("13px"),
			new OperatorNode("+"),
			new NumericNode("4")
		};

		ValueNode expected = new NumericNode("17px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddUnits2Test() {

		CssNode[] input = {
			new NumericNode("13"),
			new OperatorNode("+"),
			new NumericNode("4px")
		};

		ValueNode expected = new NumericNode("17px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalSubtractUnitsTest() {

		CssNode[] input = {
			new NumericNode("13px"),
			new OperatorNode("-"),
			new NumericNode("4")
		};

		ValueNode expected = new NumericNode("9px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalSubtractUnits2Test() {

		CssNode[] input = {
			new NumericNode("13"),
			new OperatorNode("-"),
			new NumericNode("4px")
		};

		ValueNode expected = new NumericNode("9px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyUnitsTest() {

		CssNode[] input = {
			new NumericNode("13px"),
			new OperatorNode("*"),
			new NumericNode("4")
		};

		ValueNode expected = new NumericNode("52px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyUnits2Test() {

		CssNode[] input = {
			new NumericNode("13"),
			new OperatorNode("*"),
			new NumericNode("4px")
		};

		ValueNode expected = new NumericNode("52px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalDivideUnitsTest() {

		CssNode[] input = {
			new NumericNode("13px"),
			new OperatorNode("/"),
			new NumericNode("4")
		};

		ValueNode expected = new NumericNode("3.25px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalDivideUnits2Test() {

		CssNode[] input = {
			new NumericNode("13"),
			new OperatorNode("/"),
			new NumericNode("4px")
		};

		ValueNode expected = new NumericNode("3.25px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddConflictingUnitsTest() {

		CssNode[] input = {
			new NumericNode("42px"),
			new OperatorNode("+"),
			new NumericNode("2pt")
		};

		try {
			new ArithmeticEvaluator().eval(input);
			fail("Expected InvalidNodeException");

		} catch (InvalidNodeException ex) {
			ValueNode expected = new NumericNode("2pt");
			assertEquals(expected, ex.getNode());
		}
	}

	@Test
	public void evalSubtractConflictingUnitsTest() {

		CssNode[] input = {
			new NumericNode("42em"),
			new OperatorNode("-"),
			new NumericNode("2%")
		};

		try {
			new ArithmeticEvaluator().eval(input);
			fail("Expected InvalidNodeException");

		} catch (InvalidNodeException ex) {
			ValueNode expected = new NumericNode("2%");
			assertEquals(expected, ex.getNode());
		}
	}

	@Test
	public void evalMultiplyConflictingUnitsTest() {

		CssNode[] input = {
			new NumericNode("42em"),
			new OperatorNode("*"),
			new NumericNode("2%")
		};

		try {
			new ArithmeticEvaluator().eval(input);
			fail("Expected InvalidNodeException");

		} catch (InvalidNodeException ex) {
			ValueNode expected = new NumericNode("2%");
			assertEquals(expected, ex.getNode());
		}
	}

	@Test
	public void evalDivideConflictingUnitsTest() {

		CssNode[] input = {
			new NumericNode("42%"),
			new OperatorNode("/"),
			new NumericNode("2em")
		};

		try {
			new ArithmeticEvaluator().eval(input);
			fail("Expected InvalidNodeException");

		} catch (InvalidNodeException ex) {
			ValueNode expected = new NumericNode("2em");
			assertEquals(expected, ex.getNode());
		}
	}

	@Test
	public void evalAddColorTest() {

		CssNode[] input = {
			new NumericNode("3"),
			new OperatorNode("+"),
			new ColorNode("#123")
		};

		ValueNode expected = new ColorNode("#142536");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddColor2Test() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("+"),
			new NumericNode("3")
		};

		ValueNode expected = new ColorNode("#153759");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalSubtractColorTest() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("-"),
			new NumericNode("3")
		};

		ValueNode expected = new ColorNode("#0F3153");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyColorTest() {

		CssNode[] input = {
			new NumericNode("3"),
			new OperatorNode("*"),
			new ColorNode("#231")
		};

		ValueNode expected = new ColorNode("#693");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyColor2Test() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("*"),
			new NumericNode("3")
		};

		ValueNode expected = new ColorNode("#369CFF");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalDivideColorTest() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("/"),
			new NumericNode("10")
		};

		ValueNode expected = new ColorNode("#010508");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddColorsTest() {

		CssNode[] input = {
			new ColorNode("springgreen"),
			new OperatorNode("+"),
			new ColorNode("maroon")
		};

		ValueNode expected = new ColorNode("#80FF7F");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddColors2Test() {

		CssNode[] input = {
			new ColorNode("#123"),
			new OperatorNode("+"),
			new ColorNode("#321")
		};

		ValueNode expected = new ColorNode("#444444");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalSubtractColorsTest() {

		CssNode[] input = {
			new ColorNode("#123"),
			new OperatorNode("+"),
			new ColorNode("#321")
		};

		ValueNode expected = new ColorNode("#444444");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyColorsTest() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("*"),
			new ColorNode("#040302")
		};

		ValueNode expected = new ColorNode("#489CAC");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalDivideColorsTest() {

		CssNode[] input = {
			new ColorNode("#123456"),
			new OperatorNode("/"),
			new ColorNode("#020406")
		};

		ValueNode expected = new ColorNode("#090D0E");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}
}
