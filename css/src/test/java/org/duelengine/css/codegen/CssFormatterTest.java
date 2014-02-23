package org.duelengine.css.codegen;

import static org.junit.Assert.*;

import java.io.IOException;

import org.duelengine.css.ast.*;
import org.junit.Test;

public class CssFormatterTest {

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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
					new FunctionNode(
						"-webkit-gradient",
						new ValueNode("linear"),
						new OperatorNode(","),
						new ValueNode("left"),
						new ValueNode("top"),
						new OperatorNode(","),
						new ValueNode("left"),
						new ValueNode("bottom"),
						new OperatorNode(","),
						new FunctionNode(
							"from",
							new ColorNode("#D5DDE5")),
						new OperatorNode(","),
						new FunctionNode("to",
							new ColorNode("#FFFFFF")))),
				new DeclarationNode(
					"background",
					new FunctionNode(
						"-moz-linear-gradient",
						new ValueNode("top"),
						new OperatorNode(","),
						new ColorNode("#D5DDE5"),
						new OperatorNode(","),
						new ColorNode("#FFFFFF")))
			));

		String expected =
			"body\n" +
			"{\n" +
			"\tbackground: -webkit-gradient(linear, left top, left bottom, from(#D5DDE5), to(#FFFFFF));\n" +
			"\tbackground: -moz-linear-gradient(top, #D5DDE5, #FFFFFF);\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueListComplexCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("body"),
				new DeclarationNode(
					"background",
					new FunctionNode(
						"-webkit-gradient",
						new ValueNode("linear"),
						new OperatorNode(","),
						new ValueNode("left"),
						new ValueNode("top"),
						new OperatorNode(","),
						new ValueNode("left"),
						new ValueNode("bottom"),
						new OperatorNode(","),
						new FunctionNode(
							"from",
							new ColorNode("#D5DDE5")),
						new OperatorNode(","),
						new FunctionNode(
							"to",
							new ColorNode("cornflowerblue")))),
				new DeclarationNode(
					"background",
					new FunctionNode(
						"-moz-linear-gradient",
						new ValueNode("top"),
						new OperatorNode(","),
						new ColorNode("silver"),
						new OperatorNode(","),
						new ColorNode("white")))
			));

		String expected = "body{background:-webkit-gradient(linear,left top,left bottom,from(#D5DDE5),to(#6495ED));background:-moz-linear-gradient(top,silver,#FFF);}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void valueHSLACompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
				new RuleSetNode(
						new SelectorNode("h1"),
						new DeclarationNode(
							"color",
							new FunctionNode("hsla",
								new NumericNode("0"),
								new OperatorNode(","),
								new NumericNode("0%"),
								new OperatorNode(","),
								new NumericNode("0%"),
								new OperatorNode(","),
								new NumericNode("0.0")))));
				((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)input.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(2)).setKeepUnits(true);
				((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)input.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(4)).setKeepUnits(true);

		String expected = "h1{color:hsla(0,0%,0%,0);}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode[] {
					new SelectorNode("h1"),
					new SelectorNode("h2"),
					new SelectorNode("h3"),
					new SelectorNode("h4")
				}));

		String expected = "";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorCompoundTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new AccessorNode(
						"span.foo.bar",
						new ValueNode("foo"),
						new OperatorNode("="),
						new ValueNode("bar"))),
				new DeclarationNode(
					"color",
					new ColorNode("red"))));

		String expected =
			"span.foo.bar[foo=bar]\n" +
			"{\n" +
			"\tcolor: red;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
					new AccessorNode(
						"E",
						new ValueNode("foo"),
						new OperatorNode("~="),
						new StringNode("\"warning\"")),
					new CombinatorNode(CombinatorType.CHILD),
					new ValueNode("F:first-child"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new FunctionNode(
						"G:lang",
						new ValueNode("en"))),
				new DeclarationNode(
					"color",
					new ColorNode("#336699"))));

		String expected =
			"div#my-id *.myClass E[foo~=\"warning\"] > F:first-child + G:lang(en)\n" +
			"{\n" +
			"\tcolor: #336699;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorComplexCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div#my-id"),
					new ValueNode("*.myClass"),
					new AccessorNode(
						"E",
						new ValueNode("foo"),
						new OperatorNode("~="),
						new StringNode("\"warning\"")),
					new CombinatorNode(CombinatorType.CHILD),
					new ValueNode("F:first-child"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new FunctionNode(
						"G:lang",
						new ValueNode("en"))),
				new DeclarationNode(
					"color",
					new ColorNode("#336699"))));

		String expected = "div#my-id *.myClass E[foo~=\"warning\"]>F:first-child+G:lang(en){color:#369;}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void selectorEscapedTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
				new RuleSetNode(
					new SelectorNode(
						new AccessorNode(
							"p",
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
								"}\""))),
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void importantCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("blue")).withImportant()));

		String expected =
			"h1{color:blue!important;}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleImportTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode(
				"import",
				new FunctionNode(
					"url",
					new StringNode("\"reset.css\"")),
				new ValueNode("screen")));

		String expected =
			"@import url(\"reset.css\") screen;";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaBracesTest() throws IOException {

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
			"@media screen, print {\r\n" +
			"  body {\r\n" +
			"    font-size: 10pt;\r\n" +
			"  }\r\n" +
			"  h1 {\r\n" +
			"    font-size: 14pt;\r\n" +
			"  }\r\n" +
			"  h2 {\r\n" +
			"    font-size: 12pt;\r\n" +
			"  }\r\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("  ", "\r\n", true)).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaCompactTest() throws IOException {

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
			"@media screen,print{body{font-size:10pt;}h1{font-size:14pt;}h2{font-size:12pt;}}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
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
						new FunctionNode(
							"local",
							new StringNode("'Foo'")),
						new OperatorNode(","),
						new FunctionNode(
							"url",
							new StringNode("'http://example.com/fonts/foo.tt'")),
						new FunctionNode(
							"format",
							new StringNode("'truetype'"))))));

		String expected =
			"@font-face\n" +
			"{\n" +
			"\tfont-family: 'Foo';\n" +
			"\tsrc: local('Foo'), url('http://example.com/fonts/foo.tt') format('truetype');\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaAccessorPseudoElementTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new AtRuleNode("media",
				new ValueNode[] {
					new ValueNode("print")
				},
				new BlockNode(
					new RuleSetNode(
						new SelectorNode(
							new AccessorNode("a", new ValueNode("href")),
							new CombinatorNode(CombinatorType.SELF),
							new ValueNode("::after")),
						new DeclarationNode(
							"content",
							new StringNode("\" (\""),
							new FunctionNode("attr", new ValueNode("href")),
							new StringNode("\")\""))))));

		String expected =
			"@media print{a[href]::after{content:\" (\" attr(href) \")\";}}";
	
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassFunctionTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new FunctionNode(
						"p:nth-last-of-type",
						new ValueNode("n"),
						new OperatorNode("+"),
						new ValueNode("2"))),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:nth-last-of-type(n+2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
					new FunctionNode(
						"not-last-of-type",
						new ValueNode("n"),
						new OperatorNode("+"),
						new ValueNode("2"))),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:not-last-of-type(n+2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
					new FunctionNode(
						"nth-last-of-type-fake",
						new ValueNode("n"),
						new OperatorNode("+"),
						new ValueNode("2"))),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		String expected =
			"p:nth-last-of-type-fake(n+2)\n" +
			"{\n" +
			"\tcolor: #69C;\n" +
			"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementOnAccessorTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new AccessorNode("a", new ValueNode("href")),
					new CombinatorNode(CombinatorType.SELF),
					new ValueNode(":after")),
				new DeclarationNode(
					"content",
					new StringNode("\"foo\""))));

		String expected = "a[href]:after{content:\"foo\";}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementChildOfAccessorTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new AccessorNode("a", new ValueNode("href")),
					new ValueNode(":after")),
				new DeclarationNode(
					"content",
					new StringNode("\"foo\""))));

		String expected = "a[href] :after{content:\"foo\";}";

		StringBuilder output = new StringBuilder();
		new CssFormatter().write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassComplexJoining() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new FunctionNode("*:not", new ValueNode(":first-of-type")),
					new CombinatorNode(CombinatorType.SELF),
					new FunctionNode(":not", new ValueNode(":last-of-type"))),
				new DeclarationNode(
					"display",
					new ValueNode("none"))));

		String expected =
				"*:not(:first-of-type):not(:last-of-type)\n"+
				"{\n" +
				"\tdisplay: none;\n" +
				"}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.foo"),
					new ValueNode(".1a")),
				new DeclarationNode(
					"filter",
					new ValueNode("progid"),
					new OperatorNode(":"),
					new FunctionNode(
						"DXImageTransform.Microsoft.AlphaImageLoader",
						new ValueNode("src"),
						new OperatorNode("="),
						new StringNode("'foo.png'"),
						new OperatorNode(","),
						new ValueNode("sizingMethod"),
						new OperatorNode("="),
						new StringNode("\"scale\"")))));

		String expected =
			"div.foo .1a\n" +
			"{\n" +
			"\tfilter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='foo.png', sizingMethod=\"scale\");\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void declarationFilterCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.foo"),
					new ValueNode(".1a")),
				new DeclarationNode(
					"filter",
					new ValueNode("progid"),
					new OperatorNode(":"),
					new FunctionNode(
						"DXImageTransform.Microsoft.AlphaImageLoader",
						new ValueNode("src"),
						new OperatorNode("="),
						new StringNode("'foo.png'"),
						new OperatorNode(","),
						new ValueNode("sizingMethod"),
						new OperatorNode("="),
						new StringNode("\"scale\"")))));

		String expected = "div.foo .1a{filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='foo.png',sizingMethod=\"scale\");}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
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
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void commentInlineCompactTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode(".foo")),
				new DeclarationNode(
					"padding",
					new NumericNode(1.5625, "em"),
					new CommentNode(" 25px / 16px "),
					new NumericNode(1.25, "em")),
				new CommentNode(" 20px / 16px ")));

		String expected = ".foo{padding:1.5625em 1.25em;}";

		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings()).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void commentInlineTest() throws IOException {

		StyleSheetNode input = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode(".foo")),
				new DeclarationNode(
					"padding",
					new NumericNode(1.5625, "em"),
					new CommentNode(" 25px / 16px "),
					new NumericNode(1.25, "em")),
				new CommentNode(" 20px / 16px ")));

		// actually not that pretty as rules for comments are pretty crude
		String expected =
			".foo\n" +
			"{\n" +
			"\tpadding: 1.5625em /* 25px / 16px */ 1.25em;\n" +
			"\t/* 20px / 16px */\n" +
			"}";
		
		StringBuilder output = new StringBuilder();
		new CssFormatter(new CodeGenSettings("\t", "\n")).write(output, input);
		String actual = output.toString();

		assertEquals(expected, actual);
	}
}
