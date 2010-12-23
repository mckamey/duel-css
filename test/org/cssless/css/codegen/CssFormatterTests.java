package org.cssless.css.codegen;

import static org.junit.Assert.*;
import java.io.IOException;
import org.cssless.css.ast.*;
import org.junit.Test;

public class CssFormatterTests {

	@Test
	public void valueListMixedTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
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

		String expected =
			"h1\n" +
			"{\n" +
			"\tfont: bold 2.0em/120% Helvetica, Arial, sans-serif;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueListComplexTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("body"),
				new DeclarationNode(
					"background",
					new ValueNode("-webkit-gradient("),
					new ValueNode("linear"),
					new OperatorNode(","),
					new ValueNode("left"),
					new ValueNode("top"),
					new OperatorNode(","),
					new ValueNode("left"),
					new ValueNode("bottom"),
					new OperatorNode(","),
					new ValueNode("from("),
					new ColorNode("#D5DDE5"),
					new OperatorNode(")"),
					new OperatorNode(","),
					new ValueNode("to("),
					new ColorNode("#FFFFFF"),
					new OperatorNode(")"),
					new OperatorNode(")")),
				new DeclarationNode(
					"background",
					new ValueNode("-moz-linear-gradient("),
					new ValueNode("top"),
					new OperatorNode(","),
					new ColorNode("#D5DDE5"),
					new OperatorNode(","),
					new ColorNode("#FFFFFF"),
					new OperatorNode(")"))
			));

		String expected =
			"body\n" +
			"{\n" +
			"\tbackground: -webkit-gradient(linear, left top, left bottom, from(#D5DDE5), to(#FFFFFF));\n" +
			"\tbackground: -moz-linear-gradient(top, #D5DDE5, #FFFFFF);\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatBareTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("foo#bar"),
				new DeclarationNode(
					"margin",
					new NumericNode(".2em"))));

		String expected =
			"foo#bar\n" +
			"{\n" +
			"\tmargin: .2em;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
				new RuleSetNode(
					new SelectorNode(".bar"),
					new DeclarationNode(
						"margin",
						new NumericNode("-1.2em"))));

		String expected =
			".bar\n" +
			"{\n" +
			"\tmargin: -1.2em;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatNegativeBareTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("-bar"),
				new DeclarationNode(
					"margin",
					new NumericNode("-.2em"))));

		String expected =
			"-bar\n" +
			"{\n" +
			"\tmargin: -.2em;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(".bar"),
				new DeclarationNode(
					"margin",
					new OperatorNode("+"),
					new NumericNode("1.2em"))));

		String expected =
			".bar\n" +
			"{\n" +
			"\tmargin: +1.2em;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueFloatPositiveBareTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("-bar"),
				new DeclarationNode(
					"margin",
					new OperatorNode("+"),
					new NumericNode(".2em"))));

		String expected =
			"-bar\n" +
			"{\n" +
			"\tmargin: +.2em;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1")));

		String expected =
			"h1\n" +
			"{\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetSingleTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("blue"))));

		String expected =
			"h1\n" +
			"{\n" +
			"\tcolor: blue;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetMultipleTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("red")),
				new DeclarationNode(
					"text-align",
					new ValueNode("center"))));

		String expected =
			"h1\n" +
			"{\n" +
			"\tcolor: red;\n" +
			"\ttext-align: center;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorGroupingTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode[] {
					new SelectorNode("q:before"),
					new SelectorNode("q:after")
				},
				new DeclarationNode(
					"content",
					new StringNode("''"))));

		String expected =
			"q:before, q:after\n" +
			"{\n" +
			"\tcontent: '';\n" +
			"}";
	
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorCompoundTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("span.foo.bar["),
					new ValueNode("foo"),
					new OperatorNode("="),
					new ValueNode("bar"),
					new OperatorNode("]")),
				new DeclarationNode(
					"color",
					new ColorNode("red"))));

		String expected =
			"span.foo.bar[foo=bar]\n" +
			"{\n" +
			"\tcolor: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorComplexTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div#my-id"),
					new ValueNode("*.myClass"),
					new ValueNode("E["),
					new ValueNode("foo"),
					new OperatorNode("~="),
					new StringNode("\"warning\""),
					new OperatorNode("]"),
					new CombinatorNode(CombinatorType.CHILD),
					new ValueNode("F:first-child"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new ValueNode("G:lang("),
					new ValueNode("en"),
					new OperatorNode(")")),
				new DeclarationNode(
					"color",
					new ColorNode("#336699"))));

		String expected =
			"div#my-id *.myClass E[foo~=\"warning\"] > F:first-child + G:lang(en)\n" +
			"{\n" +
			"\tcolor: #336699;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorEscapedTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
				new RuleSetNode(
					new SelectorNode(
						new ValueNode("p["),
						new ValueNode("example"),
						new OperatorNode("="),
						new StringNode(
							"\"public class foo\\\n" +
							"{\\\n" +
							"\tprivate int x;\\\n" +
							"\\\n" +
							"\tfoo(int x) {\\\n" +
							"\t\tthis.x = x;\\\n" +
							"\t}\\\n" +
							"\\\n" +
							"}\""),
						new OperatorNode("]")),
					new DeclarationNode(
						"color",
						new ColorNode("red"))));

		String expected =
			"p[example=\"public class foo\\\n" +
			"{\\\n" +
			"\tprivate int x;\\\n" +
			"\\\n" +
			"\tfoo(int x) {\\\n" +
			"\t\tthis.x = x;\\\n" +
			"\t}\\\n" +
			"\\\n" +
			"}\"]\n" +
			"{\n" +
			"\tcolor: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void importantTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("blue")).withImportant()));

		String expected =
			"h1\n" +
			"{\n" +
			"\tcolor: blue !important;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleImportTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode(
				"import",
				new ValueNode("url("),
				new StringNode("\"reset.css\""),
				new OperatorNode(")"),
				new ValueNode("screen")));

		String expected =
			"@import url(\"reset.css\") screen;";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode(
				"media",
				new ValueNode[] {
					new ValueNode("screen"),
					new OperatorNode(","),
					new ValueNode("print")
				},
				new BlockNode(
					new RuleSetNode(
						new SelectorNode("body"),
						new DeclarationNode(
							"font-size",
							new NumericNode("10pt"))),
					new RuleSetNode(
						new SelectorNode("h1"),
						new DeclarationNode(
							"font-size",
							new NumericNode("14pt"))),
					new RuleSetNode(
						new SelectorNode("h2"),
						new DeclarationNode(
							"font-size",
							new NumericNode("12pt"))))));

		String expected =
			"@media screen, print\n" +
			"{\n" +
			"\tbody\n" +
			"\t{\n" +
			"\t\tfont-size: 10pt;\n" +
			"\t}\n" +
			"\n" +
			"\th1\n" +
			"\t{\n" +
			"\t\tfont-size: 14pt;\n" +
			"\t}\n" +
			"\n" +
			"\th2\n" +
			"\t{\n" +
			"\t\tfont-size: 12pt;\n" +
			"\t}\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleFontTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode(
				"font-face",
				null,
				new BlockNode(
					new DeclarationNode(
						"font-family",
						new StringNode("'Foo'")),
					new DeclarationNode(
						"src",
						new ValueNode("local("),
						new StringNode("'Foo'"),
						new OperatorNode(")"),
						new OperatorNode(","),
						new ValueNode("url("),
						new StringNode("'http://example.com/fonts/foo.tt'"),
						new OperatorNode(")"),
						new ValueNode("format("),
						new StringNode("'truetype'"),
						new OperatorNode(")")))));

		String expected =
			"@font-face\n" +
			"{\n" +
			"\tfont-family: 'Foo';\n" +
			"\tsrc: local('Foo'), url('http://example.com/fonts/foo.tt') format('truetype');\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRulePageTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode(
				"page",
				new ValueNode[] {
					new OperatorNode(":"),
					new ValueNode("left")
				},
				new BlockNode(
					new DeclarationNode(
						"margin-left",
						new NumericNode("4cm")),
					new DeclarationNode(
						"margin-right",
						new NumericNode("3cm")))));

		String expected =
			"@page :left\n" +
			"{\n" +
			"\tmargin-left: 4cm;\n" +
			"\tmargin-right: 3cm;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("a:visited.className#id"),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"a:visited.className#id\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassFunctionTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("p:nth-last-of-type("),
					new ValueNode("n"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new NumericNode("2"),
					new OperatorNode(")")),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:nth-last-of-type(n + 2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("p"),
					new OperatorNode(":"),
					new ValueNode("not-last-of-type("),
					new ValueNode("n"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new NumericNode("2"),
					new OperatorNode(")")),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:not-last-of-type(n + 2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidSuffixTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("p"),
					new OperatorNode(":"),
					new ValueNode("nth-last-of-type-fake("),
					new ValueNode("n"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new NumericNode("2"),
					new OperatorNode(")")),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:nth-last-of-type-fake(n + 2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("p::first-line"),
				new DeclarationNode(
					"text-transform",
					new ValueNode("uppercase"))));

		String expected =
			"p::first-line\n" +
			"{\n" +
			"\ttext-transform: uppercase;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixDefaultTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("|p"),
				new DeclarationNode(
					"color",
					new ColorNode("silver"))));

		String expected =
			"|p\n" +
			"{\n" +
			"\tcolor: silver;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixAnyTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("*|p"),
				new DeclarationNode(
					"color",
					new ColorNode("purple"))));

		String expected =
			"*|p\n" +
			"{\n" +
			"\tcolor: purple;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void namespacePrefixFooTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("foo|p"),
				new DeclarationNode(
					"color",
					new ColorNode("palevioletred"))));

		String expected =
			"foo|p\n" +
			"{\n" +
			"\tcolor: palevioletred;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void dashMatchTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo"),
					new OperatorNode("|="),
					new ValueNode("p")),
				new DeclarationNode(
					"color",
					new ColorNode("lightslategrey"))));

		String expected =
			"foo|=p\n" +
			"{\n" +
			"\tcolor: lightslategrey;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.foo"),
					new NumericNode(".1a")),
				new DeclarationNode(
					"filter",
					new ValueNode("progid"),
					new OperatorNode(":"),
					new ValueNode("DXImageTransform.Microsoft.AlphaImageLoader("),
					new ValueNode("src"),
					new OperatorNode("="),
					new StringNode("'foo.png'"),
					new OperatorNode(","),
					new ValueNode("sizingMethod"),
					new OperatorNode("="),
					new StringNode("\"scale\""),
					new OperatorNode(")"))));

		String expected =
			"div.foo .1a\n" +
			"{\n" +
			"\tfilter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='foo.png', sizingMethod=\"scale\");\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	/* more hacks can be found at http://centricle.com/ref/css/filters/ */
	
	@Test
	public void hacksIE6SelectorTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("*"),
					new ValueNode("html"),
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"))));

		String expected =
			"* html div.blah\n" +
			"{\n" +
			"\tcolor: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyUnderscoreTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("div.blah"),
				new DeclarationNode(
					"_color",
					new ColorNode("red"))));

		String expected =
			"div.blah\n" +
			"{\n" +
			"\t_color: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE6PropertyDashTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("div.blah"),
				new DeclarationNode(
					"-color",
					new ColorNode("red"))));

		String expected =
			"div.blah\n" +
			"{\n" +
			"\t-color: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7SelectorTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("*"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new ValueNode("html"),
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("yellow"))));

		String expected =
			"* + html div.blah\n" +
			"{\n" +
			"\tcolor: yellow;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7PropertyStarTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div"),
					new ValueNode(".blah")),
				new DeclarationNode(
					"*color",
					new ColorNode("red"))));

		String expected =
			"div .blah\n" +
			"{\n" +
			"\t*color: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7NotImportantTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"),
					new OperatorNode("!"),
					new ValueNode("ie"))));

		String expected =
			"div.blah\n" +
			"{\n" +
			"\tcolor: red!ie;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE7ImportantTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"),
					new OperatorNode("!")).withImportant()));

		String expected =
			"div.blah\n" +
			"{\n" +
			"\tcolor: red! !important;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void hacksIE8Test() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ValueNode("#0FC\\0"),
					new OperatorNode("/"))));

		String expected =
			"div.blah\n" +
			"{\n" +
			"\tcolor: #0FC\\0/;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4NotCommentTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo")),
				new CommentNode("/"),
				new DeclarationNode(
					"property",
					new ValueNode("value")),
				new CommentNode(" ")));

		String expected =
			"foo\n" +
			"{\n" +
			"\t/*/*/\n" +
			"\tproperty: value;\n" +
			"\t/* */\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}
	
	@Test
	public void hacksNS4CommentTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo")),
				new CommentNode("/"),
				new CommentNode("/property:value;/* ")));

		String expected =
			"foo\n" +
			"{\n" +
			"\t/*/*/\n" +
			"\t/*/property:value;/* */\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void commentTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("body")),
				new CommentNode(" font-size: 10pt ")));

		String expected =
			"body\n" +
			"{\n" +
			"\t/* font-size: 10pt */\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void commentTrailingTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("body"))),
			new CommentNode("trailing comment"));

		String expected =
			"body\n" +
			"{\n" +
			"}\n" +
			"\n" +
			"/*trailing comment*/";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}
}
