package org.cssless.css.parsing;

import static org.junit.Assert.*;

import org.junit.Test;

public class CssLexerTests {

	@Test
	public void valueListTest() {

		String input = "\"Lucida Sans Unicode\" ,'Lucida Grande',Helvetica, Arial, sans-serif";

		Object[] expected = {
				CssToken.string("\"Lucida Sans Unicode\""),
				CssToken.operator(","),
				CssToken.string("'Lucida Grande'"),
				CssToken.operator(","),
				CssToken.value("Helvetica"),
				CssToken.operator(","),
				CssToken.value("Arial"),
				CssToken.operator(","),
				CssToken.value("sans-serif")
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void stringEscapeTest() {

		String input = "\"'\\\" \\\"'\",'\"\"\"\"'";

		Object[] expected = {
				CssToken.string("\"'\\\" \\\"'\""),
				CssToken.operator(","),
				CssToken.string("'\"\"\"\"'")
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueListMixedTest() {

		String input = "h1 { font: bold 2.0em/120% Helvetica, Arial, sans-serif }";

		Object[] expected = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("font"),
				CssToken.operator(":"),
				CssToken.value("bold"),
				CssToken.numeric("2.0em"),
				CssToken.operator("/"),
				CssToken.numeric("120%"),
				CssToken.value("Helvetica"),
				CssToken.operator(","),
				CssToken.value("Arial"),
				CssToken.operator(","),
				CssToken.value("sans-serif"),
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
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.value("background"),
				CssToken.operator(":"),
				CssToken.value("-webkit-gradient"),
				CssToken.operator("("),
				CssToken.value("linear"),
				CssToken.operator(","),
				CssToken.value("left"),
				CssToken.value("top"),
				CssToken.operator(","),
				CssToken.value("left"),
				CssToken.value("bottom"),
				CssToken.operator(","),
				CssToken.func("from"),
				CssToken.color("#D5DDE5"),
				CssToken.operator(")"),
				CssToken.operator(","),
				CssToken.func("to"),
				CssToken.color("#FFFFFF"),
				CssToken.operator(")"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.value("background"),
				CssToken.operator(":"),
				CssToken.func("-moz-linear-gradient"),
				CssToken.value("top"),
				CssToken.operator(","),
				CssToken.color("#D5DDE5"),
				CssToken.operator(","),
				CssToken.color("#FFFFFF"),
				CssToken.operator(")"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueFloatBareTest() {

		String input = "foo#bar{margin:.2em;}";

		Object[] expected = {
				CssToken.value("foo#bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric(".2em"),
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
				CssToken.value(".bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric("-1.2em"),
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
				CssToken.value("-bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric("-.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveTest() {

		String input = ".bar{margin:+1.2em;}";

		Object[] expected = {
				CssToken.value(".bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.operator("+"),
				CssToken.numeric("1.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveBareTest() {

		String input = "-bar{margin:+.2em;}";

		Object[] expected = {
				CssToken.value("-bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.operator("+"),
				CssToken.numeric(".2em"),
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
				CssToken.operator(":"),
				CssToken.color("blue"),
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
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),
				CssToken.value("text-align"),
				CssToken.operator(":"),
				CssToken.value("center"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void selectorGroupingTest() {

		String input = "q:before,q:after{content:''}";

		Object[] expected = {
				CssToken.value("q:before"),
				CssToken.operator(","),
				CssToken.value("q:after"),
				CssToken.blockBegin(),
				CssToken.value("content"),
				CssToken.operator(":"),
				CssToken.string("''"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void selectorCompoundTest() {

		String input = "span.foo.bar[foo=bar]{ color :red}";

		Object[] expected = {
				CssToken.accessor("span.foo.bar"),
				CssToken.value("foo"),
				CssToken.operator("="),
				CssToken.value("bar"),
				CssToken.operator("]"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void selectorComplexTest() {

		String input = "div#my-id *.myClass E[foo~=\"warning\"]>F:first-child + G:lang(en) { color : #336699;}";

		Object[] expected = {
				CssToken.value("div#my-id"),
				CssToken.value("*.myClass"),
				CssToken.accessor("E"),
				CssToken.value("foo"),
				CssToken.operator("~="),
				CssToken.string("\"warning\""),
				CssToken.operator("]"),
				CssToken.operator(">"),
				CssToken.value("F:first-child"),
				CssToken.operator("+"),
				CssToken.func("G:lang"),
				CssToken.value("en"),
				CssToken.operator(")"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#336699"),
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
				CssToken.accessor("p"),
				CssToken.value("example"),
				CssToken.operator("="),
				CssToken.string(
					"\"public class foo\\\n" +
					"{\\\n" +
					"\tprivate int x;\\\n" +
					"\\\n" +
					"\tfoo(int x) {\\\n" +
					"\t\tthis.x = x;\\\n" +
					"\t}\\\n" +
					"\\\n" +
					"}\""),
				CssToken.operator("]"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void importantTest() {

		String input = "h1 { color : blue!important}";

		Object[] expected = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
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
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
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
				CssToken.func("url"),
				CssToken.string("\"reset.css\""),
				CssToken.operator(")"),
				CssToken.value("screen"),
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
				CssToken.value("screen"),
				CssToken.operator(","),
				CssToken.value("print"),
				CssToken.blockBegin(),
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.value("font-size"),
				CssToken.operator(":"),
				CssToken.numeric("10pt"),
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
				CssToken.value("font-family"),
				CssToken.operator(":"),
				CssToken.string("'Foo'"),
				CssToken.ruleDelim(),
				CssToken.value("src"),
				CssToken.operator(":"),
				CssToken.func("local"),
				CssToken.string("'Foo'"),
				CssToken.operator(")"),
				CssToken.operator(","),
				CssToken.func("url"),
				CssToken.string("'http://example.com/fonts/foo.tt'"),
				CssToken.operator(")"),
				CssToken.func("format"),
				CssToken.string("'truetype'"),
				CssToken.operator(")"),
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
				CssToken.operator(":"),// technically is ":left" pseudo-class
				CssToken.value("left"),
				CssToken.blockBegin(),
				CssToken.value("margin-left"),
				CssToken.operator(":"),
				CssToken.numeric("4cm"),
				CssToken.ruleDelim(),
				CssToken.value("margin-right"),
				CssToken.operator(":"),
				CssToken.numeric("3cm"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void atRuleMediaAccessorPseudoElementTest() {

		String input =
			"@media print {\n" +
			"\ta[href]::after { content: \" (\" attr(href) \")\"; }\n" +
			"}";

		Object[] expected = {
			CssToken.atRule("media"),
			CssToken.value("print"),
			CssToken.blockBegin(),
			CssToken.accessor("a"),
			CssToken.value("href"),
			CssToken.value("]::after"),
			CssToken.blockBegin(),
			CssToken.value("content"),
			CssToken.operator(":"),
			CssToken.string("\" (\""),
			CssToken.func("attr"),
			CssToken.value("href"),
			CssToken.operator(")"),
			CssToken.string("\")\""),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),
			CssToken.blockEnd()
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassTest() {

		String input = "a:visited.className#id { color: #69C; }";

		Object[] expected = {
				CssToken.value("a:visited.className#id"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassIDTest() {

		String input = "#FFF:visited, #123456:hover { color: #69C; }";

		Object[] expected = {
				CssToken.value("#FFF:visited"),
				CssToken.operator(","),
				CssToken.value("#123456:hover"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassFunctionTest() {

		String input = "p:nth-last-of-type(n+2) { color:#69C; }";

		Object[] expected = {
				CssToken.func("p:nth-last-of-type"),
				CssToken.value("n"),
				CssToken.operator("+"),
				CssToken.numeric("2"),
				CssToken.operator(")"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidTest() {

		String input = "p:not-last-of-type(n+2) { color:#69C; }";

		Object[] expected = {
				CssToken.value("p"),
				CssToken.operator(":"),
				CssToken.func("not-last-of-type"),
				CssToken.value("n"),
				CssToken.operator("+"),
				CssToken.numeric("2"),
				CssToken.operator(")"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidSuffixTest() {

		String input = "p:nth-last-of-type-fake(n+2) { color:#69C; }";

		Object[] expected = {
				CssToken.value("p"),
				CssToken.operator(":"),
				CssToken.func("nth-last-of-type-fake"),
				CssToken.value("n"),
				CssToken.operator("+"),
				CssToken.numeric("2"),
				CssToken.operator(")"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoElementTest() {

		String input = "p::first-line { text-transform: uppercase; }";

		Object[] expected = {
				CssToken.value("p::first-line"),
				CssToken.blockBegin(),
				CssToken.value("text-transform"),
				CssToken.operator(":"),
				CssToken.value("uppercase"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoElementOnAccessorTest() {

		String input =
			"a[href]:after { content: \"foo\"; }";

		Object[] expected = {
			CssToken.accessor("a"),
			CssToken.value("href"),
			CssToken.value("]:after"),
			CssToken.blockBegin(),
			CssToken.value("content"),
			CssToken.operator(":"),
			CssToken.string("\"foo\""),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoElementChildOfAccessorTest() {

		String input =
			"a[href] :after { content: \"foo\"; }";

		Object[] expected = {
			CssToken.accessor("a"),
			CssToken.value("href"),
			CssToken.operator("]"),
			CssToken.value(":after"),
			CssToken.blockBegin(),
			CssToken.value("content"),
			CssToken.operator(":"),
			CssToken.string("\"foo\""),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void pseudoClassComplexJoining() {

		String input =
			"*:not( :first-of-type ):not( :last-of-type ) {\n" +
			"\tdisplay : none;\n" +
			"}";

		Object[] expected = {
			CssToken.func("*:not"),
			CssToken.value(":first-of-type"),
			CssToken.func("):not"),
			CssToken.value(":last-of-type"),
			CssToken.operator(")"),
			CssToken.blockBegin(),
			CssToken.value("display"),
			CssToken.operator(":"),
			CssToken.value("none"),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void namespacePrefixDefaultTest() {

		String input = "|p { color: silver; }";

		Object[] expected = {
				CssToken.value("|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("silver"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void namespacePrefixAnyTest() {

		String input = "*|p { color: purple; }";

		Object[] expected = {
				CssToken.value("*|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("purple"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void namespacePrefixFooTest() {

		String input = "foo|p { color: palevioletred; }";

		Object[] expected = {
				CssToken.value("foo|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("palevioletred"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void dashMatchTest() {

		String input = "foo|=p { color: lightslategrey; }";

		Object[] expected = {
				CssToken.value("foo"),
				CssToken.operator("|="),
				CssToken.value("p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("lightslategrey"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void dashMatchWhitespaceTest() {

		String input = "foo |=p { color: lime; }";

		Object[] expected = {
				CssToken.value("foo"),
				CssToken.operator("|="),
				CssToken.value("p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("lime"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void dashMatchWhitespace2Test() {

		String input = "foo|= p { color: mediumturquoise; }";

		Object[] expected = {
				CssToken.value("foo"),
				CssToken.operator("|="),
				CssToken.value("p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("mediumturquoise"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() {

		String input =
			"div.foo .1a {\r\n" +
			"  filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='foo.png', sizingMethod=\"scale\");\r\n" +
			"}";

		Object[] expected = {
				CssToken.value("div.foo"),
				CssToken.numeric(".1a"),// incorrect but context will fix
				CssToken.blockBegin(),
				CssToken.value("filter"),
				CssToken.operator(":"),
				CssToken.value("progid"),
				CssToken.operator(":"),
				CssToken.func("DXImageTransform.Microsoft.AlphaImageLoader"),
				CssToken.value("src"),
				CssToken.operator("="),
				CssToken.string("'foo.png'"),
				CssToken.operator(","),
				CssToken.value("sizingMethod"),
				CssToken.operator("="),
				CssToken.string("\"scale\""),
				CssToken.operator(")"),
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
				CssToken.value("html"),
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
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
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("_color"),
				CssToken.operator(":"),
				CssToken.color("red"),
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
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("-color"),
				CssToken.operator(":"),
				CssToken.color("red"),
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
				CssToken.operator("+"),
				CssToken.value("html"),
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("yellow"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void hacksIE7PropertyStarTest() {

		String input = "div .blah { *color:red; }";

		Object[] expected = {
				CssToken.value("div"),
				CssToken.value(".blah"),
				CssToken.blockBegin(),
				CssToken.value("*color"),
				CssToken.operator(":"),
				CssToken.color("red"),
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
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.operator("!"),
				CssToken.value("ie"),
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
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.important(),
				CssToken.operator("!"),
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
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.value("#0FC\\0"),
				CssToken.operator("/"),
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
				CssToken.value("foo"),
				CssToken.blockBegin(),
				CssToken.comment("/"),
				CssToken.value("property"),
				CssToken.operator(":"),
				CssToken.value("value"),
				CssToken.ruleDelim(),
				CssToken.comment(" "),
				CssToken.blockEnd()
			};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4CommentTest() {

		String input = "foo { /*/*//*/property:value;/* */ }";

		Object[] expected = {
				CssToken.value("foo"),
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
				CssToken.value("body"),
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
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt;"),
				CssToken.blockEnd()
			};

		CssLexer lexer = new CssLexer(input);
		lexer.setLineComments(true);
		Object[] actual = lexer.toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void commentLessEOFTest() {

		String input =
			"body {}\n" +
			"//trailing comment";

		Object[] expected = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.blockEnd(),
				CssToken.comment("trailing comment")
			};

		CssLexer lexer = new CssLexer(input);
		lexer.setLineComments(true);
		Object[] actual = lexer.toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void nestedRulesPseudoClassTest() {

		String input =
			".logo {" +
			"\twidth: 300px;" +
			"\t&:hover { text-decoration: none }" +
			"}";

		Object[] expected = {
			CssToken.value(".logo"),
			CssToken.blockBegin(),
			CssToken.value("width"),
			CssToken.operator(":"),
			CssToken.numeric("300px"),
			CssToken.ruleDelim(),
			CssToken.value("&:hover"),
			CssToken.blockBegin(),
			CssToken.value("text-decoration"),
			CssToken.operator(":"),
			CssToken.value("none"),
			CssToken.blockEnd(),
			CssToken.blockEnd()
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void lessMixinFunctionTest() {

		String input =
			".rounded_corners(@radius: 5px) {\n" +
			"\t-moz-border-radius: @radius;\n" +
			"\t-webkit-border-radius: @radius;\n" +
			"\tborder-radius: @radius;\n" +
			"}\n" +
			"#header {\n" +
			"\t.rounded_corners;\n" +
			"}\n\n" +
			"#footer {\n" +
			"\t.rounded_corners(10px);\n" +
			"}\n";

		Object[] expected = {
			CssToken.func(".rounded_corners"),
			CssToken.atRule("radius"),
			CssToken.operator(":"),
			CssToken.numeric("5px"),
			CssToken.operator(")"),
			CssToken.blockBegin(),

			CssToken.value("-moz-border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.value("-webkit-border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.value("border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.blockEnd(),

			CssToken.value("#header"),
			CssToken.blockBegin(),
			CssToken.value(".rounded_corners"),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),

			CssToken.value("#footer"),
			CssToken.blockBegin(),
			CssToken.func(".rounded_corners"),
			CssToken.numeric("10px"),
			CssToken.operator(")"),
			CssToken.ruleDelim(),
			CssToken.blockEnd()
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void lessMixinFunctionSpaceTest() {

		String input =
			".rounded_corners (@radius: 5px) {\n" +
			"\t-moz-border-radius: @radius;\n" +
			"\t-webkit-border-radius: @radius;\n" +
			"\tborder-radius: @radius;\n" +
			"}\n" +
			"#header {\n" +
			"\t.rounded_corners;\n" +
			"}\n\n" +
			"#footer {\n" +
			"\t.rounded_corners (10px);\n" +
			"}";

		Object[] expected = {
			CssToken.value(".rounded_corners"),
			CssToken.operator("("),
			CssToken.atRule("radius"),
			CssToken.operator(":"),
			CssToken.numeric("5px"),
			CssToken.operator(")"),
			CssToken.blockBegin(),

			CssToken.value("-moz-border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.value("-webkit-border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.value("border-radius"),
			CssToken.operator(":"),
			CssToken.atRule("radius"),
			CssToken.ruleDelim(),

			CssToken.blockEnd(),

			CssToken.value("#header"),
			CssToken.blockBegin(),
			CssToken.value(".rounded_corners"),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),

			CssToken.value("#footer"),
			CssToken.blockBegin(),
			CssToken.value(".rounded_corners"),
			CssToken.operator("("),
			CssToken.numeric("10px"),
			CssToken.operator(")"),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),
		};

		Object[] actual = new CssLexer(input).toList().toArray();

		assertArrayEquals(expected, actual);
	}

	@SuppressWarnings("unused")
	private void dumpLists(Object[] expected, Object[] actual) {

		for (Object token : expected) {
			System.out.println(String.valueOf(token));
		}

		for (Object token : actual) {
			System.err.println(String.valueOf(token));
		}
	}
}
