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
				CssToken.ident("Helvetica"),
				CssToken.value(","),
				CssToken.ident("Arial"),
				CssToken.value(","),
				CssToken.ident("sans-serif")
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void atRuleTest() {

		String input = "@import url(\"reset.css\") screen;";

		Object[] expected = {
				CssToken.atRule("import"),
				CssToken.ident("url"),
				CssToken.value("("),
				CssToken.value("\"reset.css\""),
				CssToken.value(")"),
				CssToken.ident("screen"),
				CssToken.ruleDelim()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyTest() {

		String input = "h1 {}";

		Object[] expected = {
				CssToken.ident("h1"),
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
				CssToken.ident("h1"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("blue"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void ruleSetMultipleTest() {

		String input = "h1 { color : red; text-align:center; }";

		Object[] expected = {
				CssToken.ident("h1"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.ruleDelim(),
				CssToken.ident("text-align"),
				CssToken.value(":"),
				CssToken.ident("center"),
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
				CssToken.ident("q"),
				CssToken.value(":"),
				CssToken.ident("before"),
				CssToken.value(","),
				CssToken.ident("q"),
				CssToken.value(":"),
				CssToken.ident("after"),
				CssToken.blockBegin(),
				CssToken.ident("content"),
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
				CssToken.ident("body"),
				CssToken.blockBegin(),
				CssToken.ident("background"),
				CssToken.value(":"),
				CssToken.ident("-webkit-gradient"),
				CssToken.value("("),
				CssToken.ident("linear"),
				CssToken.value(","),
				CssToken.ident("left"),
				CssToken.ident("top"),
				CssToken.value(","),
				CssToken.ident("left"),
				CssToken.ident("bottom"),
				CssToken.value(","),
				CssToken.ident("from"),
				CssToken.value("("),
				CssToken.value("#D5DDE5"),
				CssToken.value(")"),
				CssToken.value(","),
				CssToken.ident("to"),
				CssToken.value("("),
				CssToken.value("#FFFFFF"),
				CssToken.value(")"),
				CssToken.value(")"),
				CssToken.ruleDelim(),
				CssToken.ident("background"),
				CssToken.value(":"),
				CssToken.ident("-moz-linear-gradient"),
				CssToken.value("("),
				CssToken.ident("top"),
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
	public void complexValue2Test() {

		String input = "h1 { font: bold 2em/1.2 Helvetica, Arial, sans-serif }";

		Object[] expected = {
				CssToken.ident("h1"),
				CssToken.blockBegin(),
				CssToken.ident("font"),
				CssToken.value(":"),
				CssToken.ident("bold"),
				CssToken.value("2em"),
				CssToken.value("/"),
				CssToken.value("1.2"),
				CssToken.ident("Helvetica"),
				CssToken.value(","),
				CssToken.ident("Arial"),
				CssToken.value(","),
				CssToken.ident("sans-serif"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void mediaBlockTest() {

		String input = "@media screen, print {body {font-size: 10pt } }";

		Object[] expected = {
				CssToken.atRule("media"),
				CssToken.ident("screen"),
				CssToken.value(","),
				CssToken.ident("print"),
				CssToken.blockBegin(),
				CssToken.ident("body"),
				CssToken.blockBegin(),
				CssToken.ident("font-size"),
				CssToken.value(":"),
				CssToken.value("10pt"),
				CssToken.blockEnd(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void complexSelectorTest() {

		String input =
			"p[example=\"public class foo\\\n" +
			"{\\\n" +
			"\tprivate int x;\\\n" +
			"\\\n" +
			"\tfoo(int x) {\\\n" +
			"\t\tthis.x = x;\\\n" +
			"\t}\\\n" +
			"\\\n" +
			"}\"] { color: red }";

		Object[] expected = {
				CssToken.ident("p"),
				CssToken.value("["),
				CssToken.ident("example"),
				CssToken.value("="),
				CssToken.value(
					"\"public class foo\\\n" +
					"{\\\n" +
					"\tprivate int x;\\\n" +
					"\\\n" +
					"\tfoo(int x) {\\\n" +
					"\t\tthis.x = x;\\\n" +
					"\t}\\\n" +
					"\\\n" +
					"}\""),
				CssToken.value("]"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void commentTest() {

		String input = "body { /* font-size: 10pt */ }";

		Object[] expected = {
				CssToken.ident("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt "),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void commentLessTest() {

		String input =
			"body {\n" +
			"\t// font-size: 10pt;\n" +
			"}";

		Object[] expected = {
				CssToken.ident("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt;"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void commentLessEOFTest() {

		String input =
			"body {}\n" +
			"//trailing comment";

		Object[] expected = {
				CssToken.ident("body"),
				CssToken.blockBegin(),
				CssToken.blockEnd(),
				CssToken.comment("trailing comment")
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
