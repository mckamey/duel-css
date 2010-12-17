package org.cssless.css.parsing;

import org.junit.Test;
import static org.junit.Assert.*;

public class CssLexerTests {

	@Test
	public void valueListTest() {

		String input = "\"Lucida Sans Unicode\" ,'Lucida Grande',Helvetica, Arial, sans-serif";

		Object[] expected = {
				CssToken.value("\"Lucida Sans Unicode\""),
				CssToken.value(","),
				CssToken.value("'Lucida Grande'"),
				CssToken.value(","),
				CssToken.value("Helvetica"),
				CssToken.value(","),
				CssToken.value("Arial"),
				CssToken.value(","),
				CssToken.value("sans-serif")
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void atRuleTest() {

		String input = "@import \"reset.css\";";

		Object[] expected = {
				CssToken.atRule("import"),
				CssToken.value("\"reset.css\""),
				CssToken.ruleDelim()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyTest() {

		String input = "h1 {}";

		Object[] expected = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void ruleSetSingleTest() {

		String input = "h1 { color : blue }";

		Object[] expected = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.value(":"),
				CssToken.value("blue"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void ruleSetMultipleTest() {

		String input = "h1 { color : red; text-align:center; }";

		Object[] expected = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.value(":"),
				CssToken.value("red"),
				CssToken.ruleDelim(),
				CssToken.value("text-align"),
				CssToken.value(":"),
				CssToken.value("center"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void selectorsMultipleTest() {

		String input = "q:before,q:after{content:''}";

		Object[] expected = {
				CssToken.value("q"),
				CssToken.value(":"),
				CssToken.value("before"),
				CssToken.value(","),
				CssToken.value("q"),
				CssToken.value(":"),
				CssToken.value("after"),
				CssToken.blockBegin(),
				CssToken.value("content"),
				CssToken.value(":"),
				CssToken.value("''"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void complexValueTest() {

		String input =
			"body {"+
			"background : -webkit-gradient ( linear , left top , left bottom , from(#D5DDE5), to(#FFFFFF) );" +
			"background:-moz-linear-gradient(top,#D5DDE5,#FFFFFF)"+
			"}";

		Object[] expected = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.value("background"),
				CssToken.value(":"),
				CssToken.value("-webkit-gradient"),
				CssToken.value("("),
				CssToken.value("linear"),
				CssToken.value(","),
				CssToken.value("left"),
				CssToken.value("top"),
				CssToken.value(","),
				CssToken.value("left"),
				CssToken.value("bottom"),
				CssToken.value(","),
				CssToken.value("from"),
				CssToken.value("("),
				CssToken.value("#D5DDE5"),
				CssToken.value(")"),
				CssToken.value(","),
				CssToken.value("to"),
				CssToken.value("("),
				CssToken.value("#FFFFFF"),
				CssToken.value(")"),
				CssToken.value(")"),
				CssToken.ruleDelim(),
				CssToken.value("background"),
				CssToken.value(":"),
				CssToken.value("-moz-linear-gradient"),
				CssToken.value("("),
				CssToken.value("top"),
				CssToken.value(","),
				CssToken.value("#D5DDE5"),
				CssToken.value(","),
				CssToken.value("#FFFFFF"),
				CssToken.value(")"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void mediaBlockTest() {

		String input = "@media print {body {font-size: 10pt } }";

		Object[] expected = {
				CssToken.atRule("media"),
				CssToken.value("print"),
				CssToken.blockBegin(),
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.value("font-size"),
				CssToken.value(":"),
				CssToken.value("10pt"),
				CssToken.blockEnd(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void commentTest() {

		String input = "body { /* font-size: 10pt */ }";

		Object[] expected = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt "),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

//dumpLists(expected, actual);
		assertArrayEquals(expected, actual);
	}

	private void dumpLists(Object[] expected, Object[] actual) {

		for (Object token : expected) {
			System.out.println(token.toString());
		}

		for (Object token : actual) {
			System.err.println(token.toString());
		}
	}
}
