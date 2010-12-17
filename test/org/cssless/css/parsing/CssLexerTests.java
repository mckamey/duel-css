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
	public void valueListMixedTest() {

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
	public void valueListComplexTest() {

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
	public void valueFloatBareTest() {

		String input = "foo#bar{margin:.2em;}";

		Object[] expected = {
				CssToken.ident("foo"),
				CssToken.value("#bar"),
				CssToken.blockBegin(),
				CssToken.ident("margin"),
				CssToken.value(":"),
				CssToken.value(".2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeTest() {

		String input = ".bar{margin:-1.2em;}";

		Object[] expected = {
				CssToken.value("."),
				CssToken.ident("bar"),
				CssToken.blockBegin(),
				CssToken.ident("margin"),
				CssToken.value(":"),
				CssToken.value("-1.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeBareTest() {

		String input = "-bar{margin:-.2em;}";

		Object[] expected = {
				CssToken.ident("-bar"),
				CssToken.blockBegin(),
				CssToken.ident("margin"),
				CssToken.value(":"),
				CssToken.value("-.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
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
	public void selectorMultipleTest() {

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
	public void selectorCompoundTest() {

		String input = "span.foo.bar[foo=bar]{ color : red}";

		Object[] expected = {
				CssToken.ident("span"),
				CssToken.value("."),
				CssToken.ident("foo"),
				CssToken.value("."),
				CssToken.ident("bar"),
				CssToken.value("["),
				CssToken.ident("foo"),
				CssToken.value("="),
				CssToken.ident("bar"),
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
	public void selectorComplexTest() {

		String input = "div#my-id *.myClass E[foo~=\"warning\"]>F:first-child + G:lang(en) { color : #336699;}";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("#my-id"),
				CssToken.value("*"),
				CssToken.value("."),
				CssToken.ident("myClass"),
				CssToken.ident("E"),
				CssToken.value("["),
				CssToken.ident("foo"),
				CssToken.value("~="),
				CssToken.value("\"warning\""),
				CssToken.value("]"),
				CssToken.value(">"),
				CssToken.ident("F"),
				CssToken.value(":"),
				CssToken.ident("first-child"),
				CssToken.value("+"),
				CssToken.ident("G"),
				CssToken.value(":"),
				CssToken.ident("lang"),
				CssToken.value("("),
				CssToken.ident("en"),
				CssToken.value(")"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.value("#336699"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void selectorEscapedTest() {

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
	public void importantTest() {

		String input = "h1 { color : blue!important}";

		Object[] expected = {
				CssToken.ident("h1"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("blue"),
				CssToken.important(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void importantWhitespaceTest() {

		String input = "h1 { color : blue ! important ;}";

		Object[] expected = {
				CssToken.ident("h1"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("blue"),
				CssToken.important(),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void atRuleImportTest() {

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
	public void atRuleMediaTest() {

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
	public void atRuleFontTest() {

		String input =
			"@font-face {\n" +
			"\tfont-family: 'Foo';\n" +
			"\tsrc: local('Foo'), url('http://example.com/fonts/foo.tt') format('truetype');\n" +
			"}";

		Object[] expected = {
				CssToken.atRule("font-face"),
				CssToken.blockBegin(),
				CssToken.ident("font-family"),
				CssToken.value(":"),
				CssToken.value("'Foo'"),
				CssToken.ruleDelim(),
				CssToken.ident("src"),
				CssToken.value(":"),
				CssToken.ident("local"),
				CssToken.value("("),
				CssToken.value("'Foo'"),
				CssToken.value(")"),
				CssToken.value(","),
				CssToken.ident("url"),
				CssToken.value("("),
				CssToken.value("'http://example.com/fonts/foo.tt'"),
				CssToken.value(")"),
				CssToken.ident("format"),
				CssToken.value("("),
				CssToken.value("'truetype'"),
				CssToken.value(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void atRulePageTest() {

		String input =
			"@page :left {\r\n" +
			"    margin-left: 4cm;\r\n" +
			"    margin-right: 3cm;\r\n" +
			"}\r\n";

		Object[] expected = {
				CssToken.atRule("page"),
				CssToken.value(":"),
				CssToken.ident("left"),
				CssToken.blockBegin(),
				CssToken.ident("margin-left"),
				CssToken.value(":"),
				CssToken.value("4cm"),
				CssToken.ruleDelim(),
				CssToken.ident("margin-right"),
				CssToken.value(":"),
				CssToken.value("3cm"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() {

		String input =
			"div.1a {\r\n" +
			"  filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='foo.png', sizingMethod=\"scale\");\r\n" +
			"}";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value(".1a"),
				CssToken.blockBegin(),
				CssToken.ident("filter"),
				CssToken.value(":"),
				CssToken.ident("progid"),
				CssToken.value(":"),
				CssToken.ident("DXImageTransform"),
				CssToken.value("."),
				CssToken.ident("Microsoft"),
				CssToken.value("."),
				CssToken.ident("AlphaImageLoader"),
				CssToken.value("("),
				CssToken.ident("src"),
				CssToken.value("="),
				CssToken.value("'foo.png'"),
				CssToken.value(","),
				CssToken.ident("sizingMethod"),
				CssToken.value("="),
				CssToken.value("\"scale\""),
				CssToken.value(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	/* more hacks can be found at http://centricle.com/ref/css/filters/ */
	
	@Test
	public void hacksIE6SelectorTest() {

		String input = "* html div.blah { color:red; }";

		Object[] expected = {
				CssToken.value("*"),
				CssToken.ident("html"),
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyUnderscoreTest() {

		String input = "div.blah { _color:red; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("_color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyDashTest() {

		String input = "div.blah { -color:red; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("-color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE7SelectorTest() {

		String input = "*+html div.blah { color:yellow; }";

		Object[] expected = {
				CssToken.value("*"),
				CssToken.value("+html"),
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("yellow"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE7PropertyStarTest() {

		String input = "div.blah { *color:red; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.value("*"),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE7NotImportantTest() {

		String input = "div.blah { color:red !ie; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.value("!"),
				CssToken.ident("ie"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE7ImportantTest() {

		String input = "div.blah { color:red !important!; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.ident("red"),
				CssToken.important(),
				CssToken.value("!"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE8Test() {

		String input = "div.blah { color: #0FC\\0/; }";

		Object[] expected = {
				CssToken.ident("div"),
				CssToken.value("."),
				CssToken.ident("blah"),
				CssToken.blockBegin(),
				CssToken.ident("color"),
				CssToken.value(":"),
				CssToken.value("#0FC\\0"),
				CssToken.value("/"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4NotCommentTest() {

		String input = "foo { /*/*/property:value;/* */ }";

		Object[] expected = {
				CssToken.ident("foo"),
				CssToken.blockBegin(),
				CssToken.comment("/"),
				CssToken.ident("property"),
				CssToken.value(":"),
				CssToken.ident("value"),
				CssToken.ruleDelim(),
				CssToken.comment(" "),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

//dumpLists(expected, actual);
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4CommentTest() {

		String input = "foo { /*/*//*/property:value;/* */ }";

		Object[] expected = {
				CssToken.ident("foo"),
				CssToken.blockBegin(),
				CssToken.comment("/"),
				CssToken.comment("/property:value;/* "),
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
