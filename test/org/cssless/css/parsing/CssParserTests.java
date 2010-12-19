package org.cssless.css.parsing;

import java.io.IOException;

import org.cssless.css.ast.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class CssParserTests {

	@Test
	public void valueListMixedTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(new ValueNode("h1")),
				new DeclarationNode(
					"font",
					new ValueNode("bold"),
					new NumericNode("2.0em"),
					new OperatorNode("/"),
					new NumericNode("120%"),
					new ValueNode("Helvetica"),
					new OperatorNode(","),
					new ValueNode("Arial"),
					new OperatorNode(","),
					new ValueNode("sans-serif"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueListComplexTest() throws IOException {

		CssToken[] input = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.value("background"),
				CssToken.operator(":"),
				CssToken.value("-webkit-gradient("),
				CssToken.value("linear"),
				CssToken.operator(","),
				CssToken.value("left"),
				CssToken.value("top"),
				CssToken.operator(","),
				CssToken.value("left"),
				CssToken.value("bottom"),
				CssToken.operator(","),
				CssToken.value("from("),
				CssToken.color("#D5DDE5"),
				CssToken.operator(")"),
				CssToken.operator(","),
				CssToken.value("to("),
				CssToken.color("#FFFFFF"),
				CssToken.operator(")"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.value("background"),
				CssToken.operator(":"),
				CssToken.value("-moz-linear-gradient("),
				CssToken.value("top"),
				CssToken.operator(","),
				CssToken.color("#D5DDE5"),
				CssToken.operator(","),
				CssToken.color("#FFFFFF"),
				CssToken.operator(")"),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();
		
		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatBareTest() throws IOException {

		CssToken[] input = {
				CssToken.value("foo#bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric(".2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeTest() throws IOException {

		CssToken[] input = {
				CssToken.value(".bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric("-1.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeBareTest() throws IOException {

		CssToken[] input = {
				CssToken.value("-bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.numeric("-.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveTest() throws IOException {

		CssToken[] input = {
				CssToken.value(".bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.operator("+"),
				CssToken.numeric("1.2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveBareTest() throws IOException {

		CssToken[] input = {
				CssToken.value("-bar"),
				CssToken.blockBegin(),
				CssToken.value("margin"),
				CssToken.operator(":"),
				CssToken.operator("+"),
				CssToken.numeric(".2em"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetSingleTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetMultipleTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorGroupingTest() throws IOException {

		CssToken[] input = {
				CssToken.value("q:before"),
				CssToken.operator(","),
				CssToken.value("q:after"),
				CssToken.blockBegin(),
				CssToken.value("content"),
				CssToken.operator(":"),
				CssToken.string("''"),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorCompoundTest() throws IOException {

		CssToken[] input = {
				CssToken.value("span.foo.bar["),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorComplexTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div#my-id"),
				CssToken.value("*.myClass"),
				CssToken.value("E["),
				CssToken.value("foo"),
				CssToken.operator("~="),
				CssToken.string("\"warning\""),
				CssToken.operator("]"),
				CssToken.operator(">"),
				CssToken.value("F:first-child"),
				CssToken.operator("+"),
				CssToken.value("G:lang("),
				CssToken.value("en"),
				CssToken.operator(")"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#336699"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorEscapedTest() throws IOException {

		CssToken[] input = {
				CssToken.value("p["),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void importantTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
				CssToken.important(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void importantWhitespaceTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
				CssToken.important(),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleImportTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("import"),
				CssToken.value("url("),
				CssToken.string("\"reset.css\""),
				CssToken.operator(")"),
				CssToken.value("screen"),
				CssToken.ruleDelim()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleFontTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("font-face"),
				CssToken.blockBegin(),
				CssToken.value("font-family"),
				CssToken.operator(":"),
				CssToken.string("'Foo'"),
				CssToken.ruleDelim(),
				CssToken.value("src"),
				CssToken.operator(":"),
				CssToken.value("local("),
				CssToken.string("'Foo'"),
				CssToken.operator(")"),
				CssToken.operator(","),
				CssToken.value("url("),
				CssToken.string("'http://example.com/fonts/foo.tt'"),
				CssToken.operator(")"),
				CssToken.value("format("),
				CssToken.string("'truetype'"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRulePageTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassTest() throws IOException {

		CssToken[] input = {
				CssToken.value("a:visited.className#id"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#69C"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassFunctionTest() throws IOException {

		CssToken[] input = {
				CssToken.value("p:nth-last-of-type("),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidTest() throws IOException {

		CssToken[] input = {
				CssToken.value("p"),
				CssToken.operator(":"),
				CssToken.value("not-last-of-type("),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidSuffixTest() throws IOException {

		CssToken[] input = {
				CssToken.value("p"),
				CssToken.operator(":"),
				CssToken.value("nth-last-of-type-fake("),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementTest() throws IOException {

		CssToken[] input = {
				CssToken.value("p::first-line"),
				CssToken.blockBegin(),
				CssToken.value("text-transform"),
				CssToken.operator(":"),
				CssToken.value("uppercase"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixDefaultTest() throws IOException {

		CssToken[] input = {
				CssToken.value("|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("silver"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixAnyTest() throws IOException {

		CssToken[] input = {
				CssToken.value("*|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("purple"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixFooTest() throws IOException {

		CssToken[] input = {
				CssToken.value("foo|p"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("palevioletred"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void dashMatchTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void dashMatchWhitespaceTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void dashMatchWhitespace2Test() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.foo"),
				CssToken.numeric(".1a"),// incorrect but context will fix
				CssToken.blockBegin(),
				CssToken.value("filter"),
				CssToken.operator(":"),
				CssToken.value("progid"),
				CssToken.operator(":"),
				CssToken.value("DXImageTransform.Microsoft.AlphaImageLoader("),
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	/* more hacks can be found at http://centricle.com/ref/css/filters/ */
	
	@Test
	public void hacksIE6SelectorTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyUnderscoreTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("_color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyDashTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("-color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7SelectorTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7PropertyStarTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div"),
				CssToken.value(".blah"),
				CssToken.blockBegin(),
				CssToken.value("*color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7NotImportantTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.value("!"),
				CssToken.value("ie"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7ImportantTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.important(),
				CssToken.value("!"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE8Test() throws IOException {

		CssToken[] input = {
				CssToken.value("div.blah"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.value("#0FC\\0"),
				CssToken.operator("/"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4NotCommentTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4CommentTest() throws IOException {

		CssToken[] input = {
				CssToken.value("foo"),
				CssToken.blockBegin(),
				CssToken.comment("/"),
				CssToken.comment("/property:value;/* "),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void commentTest() throws IOException {

		CssToken[] input = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt "),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void commentLessTest() throws IOException {

		CssToken[] input = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.comment(" font-size: 10pt;"),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void commentLessEOFTest() throws IOException {

		CssToken[] input = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.blockEnd(),
				CssToken.comment("trailing comment")
			};

		StyleSheetNode expected = new StyleSheetNode();

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}
}
