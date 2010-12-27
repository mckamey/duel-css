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

	@Test
	public void evalAddUnitsTest() throws IOException {

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
	public void evalAddUnits2Test() throws IOException {

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
	public void evalSubtractUnitsTest() throws IOException {

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
	public void evalSubtractUnits2Test() throws IOException {

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
	public void evalMultiplyUnitsTest() throws IOException {

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
	public void evalMultiplyUnits2Test() throws IOException {

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
	public void evalDivideUnitsTest() throws IOException {

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
	public void evalDivideUnits2Test() throws IOException {

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
	public void evalAddConflictingUnitsTest() throws IOException {

		CssNode[] input = {
			new NumericNode("42px"),
			new OperatorNode("+"),
			new NumericNode("2pt")
		};

		ValueNode expected = new NumericNode("44px");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalSubtractConflictingUnitsTest() throws IOException {

		CssNode[] input = {
			new NumericNode("42em"),
			new OperatorNode("-"),
			new NumericNode("2%")
		};

		ValueNode expected = new NumericNode("40em");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalMultiplyConflictingUnitsTest() throws IOException {

		CssNode[] input = {
			new NumericNode("42em"),
			new OperatorNode("*"),
			new NumericNode("2%")
		};

		ValueNode expected = new NumericNode("84em");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalDivideConflictingUnitsTest() throws IOException {

		CssNode[] input = {
			new NumericNode("42%"),
			new OperatorNode("/"),
			new NumericNode("2em")
		};

		ValueNode expected = new NumericNode("21%");

		ValueNode actual = new ArithmeticEvaluator().eval(input);

		assertEquals(expected, actual);
	}

	@Test
	public void evalAddColorTest() throws IOException {

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
	public void evalAddColor2Test() throws IOException {

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
	public void evalSubtractColorTest() throws IOException {

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
	public void evalMultiplyColorTest() throws IOException {

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
	public void evalMultiplyColor2Test() throws IOException {

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
	public void evalDivideColorTest() throws IOException {

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
	public void evalAddColorsTest() throws IOException {

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
	public void evalAddColors2Test() throws IOException {

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
	public void evalSubtractColorsTest() throws IOException {

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
	public void evalMultiplyColorsTest() throws IOException {

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
	public void evalDivideColorsTest() throws IOException {

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
