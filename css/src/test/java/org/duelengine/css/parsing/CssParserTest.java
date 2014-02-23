package org.duelengine.css.parsing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.duelengine.css.ast.AccessorNode;
import org.duelengine.css.ast.AtRuleNode;
import org.duelengine.css.ast.BlockNode;
import org.duelengine.css.ast.ColorNode;
import org.duelengine.css.ast.CombinatorNode;
import org.duelengine.css.ast.CombinatorType;
import org.duelengine.css.ast.CommentNode;
import org.duelengine.css.ast.DeclarationNode;
import org.duelengine.css.ast.FunctionNode;
import org.duelengine.css.ast.NumericNode;
import org.duelengine.css.ast.OperatorNode;
import org.duelengine.css.ast.RuleSetNode;
import org.duelengine.css.ast.SelectorNode;
import org.duelengine.css.ast.StringNode;
import org.duelengine.css.ast.StyleSheetNode;
import org.duelengine.css.ast.ValueNode;
import org.duelengine.css.parsing.CssParser.Syntax;
import org.junit.*;

public class CssParserTest {

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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueHSLTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.func("hsl"),
				CssToken.numeric("0"),
				CssToken.operator(","),
				CssToken.numeric("0%"),
				CssToken.operator(","),
				CssToken.numeric("0%"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new FunctionNode("hsl",
						new NumericNode("0"),
						new OperatorNode(","),
						new NumericNode("0%"),
						new OperatorNode(","),
						new NumericNode("0%")))));
		((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)expected.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(2)).setKeepUnits(true);
		((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)expected.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(4)).setKeepUnits(true);

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void valueHSLATest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.func("hsla"),
				CssToken.numeric("0"),
				CssToken.operator(","),
				CssToken.numeric("0%"),
				CssToken.operator(","),
				CssToken.numeric("0%"),
				CssToken.operator(","),
				CssToken.numeric("0.0"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
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
		((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)expected.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(2)).setKeepUnits(true);
		((NumericNode)((FunctionNode)((DeclarationNode)((RuleSetNode)expected.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getContainer().getChildren().get(4)).setKeepUnits(true);

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
				CssToken.func("-webkit-gradient"),
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("foo#bar"),
				new DeclarationNode(
					"margin",
					new NumericNode(".2em"))));

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

		StyleSheetNode expected = new StyleSheetNode(
				new RuleSetNode(
					new SelectorNode(".bar"),
					new DeclarationNode(
						"margin",
						new NumericNode("-1.2em"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("-bar"),
				new DeclarationNode(
					"margin",
					new NumericNode("-.2em"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(".bar"),
				new DeclarationNode(
					"margin",
					new OperatorNode("+"),
					new NumericNode("1.2em"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("-bar"),
				new DeclarationNode(
					"margin",
					new OperatorNode("+"),
					new NumericNode(".2em"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1")));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void ruleSetEmptyWithDelimsTest() throws IOException {

		CssToken[] input = {
				CssToken.value("h1"),
				CssToken.blockBegin(),
				CssToken.ruleDelim(),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1")));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("blue"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("red")),
				new DeclarationNode(
					"text-align",
					new ValueNode("center"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode[] {
					new SelectorNode("q:before"),
					new SelectorNode("q:after")
				},
				new DeclarationNode(
					"content",
					new StringNode("''"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorCompoundTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorComplexTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void selectorEscapedTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("h1"),
				new DeclarationNode(
					"color",
					new ColorNode("blue")).withImportant()));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleImportTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("import"),
				CssToken.func("url"),
				CssToken.string("\"reset.css\""),
				CssToken.operator(")"),
				CssToken.value("screen"),
				CssToken.ruleDelim()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new AtRuleNode(
				"import",
				new FunctionNode(
					"url",
					new StringNode("\"reset.css\"")),
				new ValueNode("screen")));

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

		StyleSheetNode expected = new StyleSheetNode(
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
							new NumericNode("10pt"))))));

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
				CssToken.func("local"),
				CssToken.string("'☺'"),
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

		StyleSheetNode expected = new StyleSheetNode(
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
								new StringNode("'☺'")),
							new OperatorNode(","),
							new FunctionNode(
								"url",
								new StringNode("'http://example.com/fonts/foo.tt'")),
							new FunctionNode(
								"format",
								new StringNode("'truetype'"))))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleFontDataURITest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("font-face"),
				CssToken.blockBegin(),
				CssToken.value("font-family"),
				CssToken.operator(":"),
				CssToken.string("'Foo'"),
				CssToken.ruleDelim(),
				CssToken.value("src"),
				CssToken.operator(":"),
				CssToken.func("url"),
				CssToken.value("data"),
				CssToken.operator(":"),
				CssToken.value("font"),
				CssToken.operator("/"),
				CssToken.value("woff"),
				CssToken.ruleDelim(),
				CssToken.value("charset"),
				CssToken.operator("="),
				CssToken.value("utf-8"),
				CssToken.ruleDelim(),
				CssToken.value("base64"),
				CssToken.operator(","),
				CssToken.value("d09GRgABAAABGRlRNAAABMAAAABwAAAAcWF3wvkdERUYAAAFMAAAAHQAAACAFKznYCbGdhc3AAAAJgAAAACAAAAAj"),
				CssToken.operator(")"),
				CssToken.func("format"),
				CssToken.string("'truetype'"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
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
								"url",
								new ValueNode("data"),
								new OperatorNode(":"),
								new ValueNode("font"),
								new OperatorNode("/"),
								new ValueNode("woff"),
								new OperatorNode(";"),
								new ValueNode("charset"),
								new OperatorNode("="),
								new ValueNode("utf-8"),
								new OperatorNode(";"),
								new ValueNode("base64"),
								new OperatorNode(","),
								new ValueNode("d09GRgABAAABGRlRNAAABMAAAABwAAAAcWF3wvkdERUYAAAFMAAAAHQAAACAFKznYCbGdhc3AAAAJgAAAACAAAAAj")),
							new FunctionNode(
								"format",
								new StringNode("'truetype'"))))));

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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleVendorKeyframesTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("-moz-keyframes"),
				CssToken.value("fooName"),
				CssToken.blockBegin(),
				CssToken.value("from"),
				CssToken.blockBegin(),
				CssToken.value("-moz-transform"),
				CssToken.operator(":"),
				CssToken.func("translate"),
				CssToken.numeric("0"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),
				CssToken.value("to"),
				CssToken.blockBegin(),
				CssToken.value("-moz-transform"),
				CssToken.operator(":"),
				CssToken.func("translate"),
				CssToken.numeric("100%"),
				CssToken.operator(")"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new AtRuleNode(
				"-moz-keyframes",
				new ValueNode[] {
					new ValueNode("fooName"),
				},
				new BlockNode(
					new RuleSetNode(
						new SelectorNode("from"),
						new DeclarationNode(
							"-moz-transform",
							new FunctionNode("translate", new NumericNode(0, "")))),
					new RuleSetNode(
						new SelectorNode("to"),
						new DeclarationNode(
							"-moz-transform",
							new FunctionNode("translate", new NumericNode(100, "%")))))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void atRuleMediaAccessorPseudoElementTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("a:visited.className#id"),
				new DeclarationNode(
					"color",
					new ColorNode("#69C"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassFunctionTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassInvalidSuffixTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("p::first-line"),
				new DeclarationNode(
					"text-transform",
					new ValueNode("uppercase"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementOnAccessorTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new AccessorNode("a", new ValueNode("href")),
					new CombinatorNode(CombinatorType.SELF),
					new ValueNode(":after")),
				new DeclarationNode(
					"content",
					new StringNode("\"foo\""))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoElementChildOfAccessorTest() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new AccessorNode("a", new ValueNode("href")),
					new ValueNode(":after")),
				new DeclarationNode(
					"content",
					new StringNode("\"foo\""))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void pseudoClassComplexJoining() throws IOException {

		CssToken[] input = {
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new FunctionNode("*:not", new ValueNode(":first-of-type")),
					new CombinatorNode(CombinatorType.SELF),
					new FunctionNode(":not", new ValueNode(":last-of-type"))),
				new DeclarationNode(
					"display",
					new ValueNode("none"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("|p"),
				new DeclarationNode(
					"color",
					new ColorNode("silver"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("*|p"),
				new DeclarationNode(
					"color",
					new ColorNode("purple"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("foo|p"),
				new DeclarationNode(
					"color",
					new ColorNode("palevioletred"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo"),
					new OperatorNode("|="),
					new ValueNode("p")),
				new DeclarationNode(
					"color",
					new ColorNode("lightslategrey"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void declarationFilterTest() throws IOException {

		CssToken[] input = {
				CssToken.value("div.foo"),
				CssToken.numeric(".1a"),// incorrect but context will fix?
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

		StyleSheetNode expected = new StyleSheetNode(
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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("*"),
					new ValueNode("html"),
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("div.blah"),
				new DeclarationNode(
					"_color",
					new ColorNode("red"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("div.blah"),
				new DeclarationNode(
					"-color",
					new ColorNode("red"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("*"),
					new CombinatorNode(CombinatorType.ADJACENT),
					new ValueNode("html"),
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("yellow"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div"),
					new ValueNode(".blah")),
				new DeclarationNode(
					"*color",
					new ColorNode("red"))));

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
				CssToken.operator("!"),
				CssToken.value("ie"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"),
					new OperatorNode("!"),
					new ValueNode("ie"))));

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
				CssToken.operator("!"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ColorNode("red"),
					new OperatorNode("!")).withImportant()));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("div.blah")),
				new DeclarationNode(
					"color",
					new ValueNode("#0FC\\0"),
					new OperatorNode("/"))));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo")),
				new CommentNode("/"),
				new DeclarationNode(
					"property",
					new ValueNode("value")),
				new CommentNode(" ")));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("foo")),
				new CommentNode("/"),
				new CommentNode("/property:value;/* ")));

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

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("body")),
				new CommentNode(" font-size: 10pt ")));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void commentTrailingTest() throws IOException {

		CssToken[] input = {
				CssToken.value("body"),
				CssToken.blockBegin(),
				CssToken.blockEnd(),
				CssToken.comment("trailing comment")
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("body"))),
			new CommentNode("trailing comment"));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void commentInlineTest() throws IOException {

		CssToken[] input = {
				CssToken.value(".foo"),
				CssToken.blockBegin(),
				CssToken.value("padding"),
				CssToken.operator(":"),
				CssToken.numeric("1.5625em"),
				CssToken.comment(" 25px / 16px "),
				CssToken.numeric("1.25em"),
				CssToken.ruleDelim(),
				CssToken.comment(" 20px / 16px "),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(
					new ValueNode(".foo")),
				new DeclarationNode(
					"padding",
					new NumericNode(1.5625, "em"),
					new CommentNode(" 25px / 16px "),
					new NumericNode(1.25, "em")),
				new CommentNode(" 20px / 16px ")));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessVariablesTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("brand_color"),
				CssToken.operator(":"),
				CssToken.color("#4D926F"),
				CssToken.ruleDelim(),

				CssToken.value("#header"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.atRule("brand_color"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),
				CssToken.value("h2"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.atRule("brand_color"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("#4D926F"))),
			new RuleSetNode(
				new SelectorNode("h2"),
				new DeclarationNode(
					"color",
					new ColorNode("#4D926F"))));

		StyleSheetNode actual = new CssParser(Syntax.LESS).parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessVariablesReferenceVarsTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("nice-blue"),
				CssToken.operator(":"),
				CssToken.color("#5B83AD"),
				CssToken.ruleDelim(),

				CssToken.atRule("light-blue"),
				CssToken.operator(":"),
				CssToken.atRule("nice-blue"),
				CssToken.operator("+"),
				CssToken.color("#111"),
				CssToken.ruleDelim(),

				CssToken.value("#header"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.atRule("light-blue"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("#6c94be"))));

		StyleSheetNode actual = new CssParser(Syntax.LESS).parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessOperationsTest() throws IOException {

		CssToken[] input = {
				CssToken.atRule("the-border"),
				CssToken.operator(":"),
				CssToken.numeric("1px"),
				CssToken.ruleDelim(),

				CssToken.atRule("base-color"),
				CssToken.operator(":"),
				CssToken.color("#111"),
				CssToken.ruleDelim(),

				CssToken.value("#header"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.atRule("base-color"),
				CssToken.operator("*"),
				CssToken.numeric("3"),
				CssToken.ruleDelim(),
				CssToken.value("border-left"),
				CssToken.operator(":"),
				CssToken.atRule("the-border"),
				CssToken.ruleDelim(),
				CssToken.value("border-right"),
				CssToken.operator(":"),
				CssToken.atRule("the-border"),
				CssToken.operator("*"),
				CssToken.numeric("2"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.value("#footer"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.operator("("),
				CssToken.atRule("base-color"),
				CssToken.operator("+"),
				CssToken.color("#111"),
				CssToken.operator(")"),
				CssToken.operator("*"),
				CssToken.numeric("1.5"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("#333333")),
				new DeclarationNode(
					"border-left",
					new NumericNode("1px")),
				new DeclarationNode(
					"border-right",
					new NumericNode("2px"))),
			new RuleSetNode(
				new SelectorNode("#footer"),
				new DeclarationNode(
					"color",
					new ColorNode("#333333"))));

		StyleSheetNode actual = new CssParser(Syntax.LESS).parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessOperations2Test() throws IOException {

		CssToken[] input = {
				CssToken.atRule("base"),
				CssToken.operator(":"),
				CssToken.numeric("5%"),
				CssToken.ruleDelim(),

				CssToken.atRule("filler"),
				CssToken.operator(":"),
				CssToken.atRule("base"),
				CssToken.operator("*"),
				CssToken.numeric("2"),
				CssToken.ruleDelim(),

				CssToken.atRule("other"),
				CssToken.operator(":"),
				CssToken.atRule("base"),
				CssToken.operator("+"),
				CssToken.atRule("filler"),
				CssToken.ruleDelim(),

				CssToken.atRule("base-color"),
				CssToken.operator(":"),
				CssToken.color("gray"),
				CssToken.ruleDelim(),

				CssToken.value("#foo"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("#888"),
				CssToken.operator("/"),
				CssToken.numeric("4"),
				CssToken.ruleDelim(),
				CssToken.value("background-color"),
				CssToken.operator(":"),
				CssToken.atRule("base-color"),
				CssToken.operator("+"),
				CssToken.color("#111"),
				CssToken.ruleDelim(),
				CssToken.value("height"),
				CssToken.operator(":"),
				CssToken.numeric("100%"),
				CssToken.operator("/"),
				CssToken.numeric("2"),
				CssToken.operator("+"),
				CssToken.atRule("other"),
				CssToken.ruleDelim(),
				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#foo"),
				new DeclarationNode(
					"color",
					new ColorNode("#222")),
				new DeclarationNode(
					"background-color",
					new ColorNode("#919191")),
				new DeclarationNode(
					"height",
					new NumericNode("65%"))));

		StyleSheetNode actual = new CssParser(Syntax.LESS).parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessNestedRulesTest() throws IOException {

		CssToken[] input = {
				CssToken.value("#header"),
				CssToken.blockBegin(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),

				CssToken.value("a"),
				CssToken.blockBegin(),
				CssToken.value("font-weight"),
				CssToken.operator(":"),
				CssToken.value("bold"),
				CssToken.ruleDelim(),
				CssToken.value("text-decoration"),
				CssToken.operator(":"),
				CssToken.value("none"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("red"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode("a")),
				new DeclarationNode(
					"font-weight",
					new ValueNode("bold")),
				new DeclarationNode(
					"text-decoration",
					new ValueNode("none"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessDeeplyNestedRulesTest() throws IOException {

		CssToken[] input = {
				CssToken.value("#header"),
				CssToken.blockBegin(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),

				CssToken.value("div.foo"),
				CssToken.blockBegin(),

				CssToken.value("background-color"),
				CssToken.operator(":"),
				CssToken.color("yellow"),
				CssToken.ruleDelim(),

				CssToken.value("a"),
				CssToken.blockBegin(),
				CssToken.value("font-weight"),
				CssToken.operator(":"),
				CssToken.value("bold"),
				CssToken.ruleDelim(),
				CssToken.value("text-decoration"),
				CssToken.operator(":"),
				CssToken.value("none"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("black"),
				CssToken.ruleDelim(),

				CssToken.blockEnd(),

				CssToken.value("background-color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
				CssToken.ruleDelim(),

				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("red")),
				new DeclarationNode(
					"background-color",
					new ColorNode("blue"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode("div.foo")),
				new DeclarationNode(
					"background-color",
					new ColorNode("yellow")),
				new DeclarationNode(
					"color",
					new ColorNode("black"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode("div.foo"),
					new ValueNode("a")),
				new DeclarationNode(
					"font-weight",
					new ValueNode("bold")),
				new DeclarationNode(
					"text-decoration",
					new ValueNode("none"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessNestedRulesPseudoClassTest() throws IOException {

		CssToken[] input = {
				CssToken.value("#header"),
				CssToken.blockBegin(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("black"),
				CssToken.ruleDelim(),

				CssToken.value(".navigation"),
				CssToken.blockBegin(),
				CssToken.value("font-size"),
				CssToken.operator(":"),
				CssToken.numeric("12px"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.value(".logo"),
				CssToken.blockBegin(),
				CssToken.value("width"),
				CssToken.operator(":"),
				CssToken.numeric("300px"),
				CssToken.ruleDelim(),
				CssToken.value(":hover"),
				CssToken.blockBegin(),
				CssToken.value("text-decoration"),
				CssToken.operator(":"),
				CssToken.value("none"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.blockEnd(),

				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("black"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".navigation")),
				new DeclarationNode(
					"font-size",
					new NumericNode("12px"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".logo")),
				new DeclarationNode(
					"width",
					new NumericNode("300px"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".logo"),
					new ValueNode(":hover")),
				new DeclarationNode(
					"text-decoration",
					new ValueNode("none"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessNestedRulesMergedPseudoClassTest() throws IOException {

		CssToken[] input = {
				CssToken.value("#header"),
				CssToken.blockBegin(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("black"),
				CssToken.ruleDelim(),

				CssToken.value(".navigation"),
				CssToken.blockBegin(),
				CssToken.value("font-size"),
				CssToken.operator(":"),
				CssToken.numeric("12px"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

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
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.blockEnd(),

				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"color",
					new ColorNode("black"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".navigation")),
				new DeclarationNode(
					"font-size",
					new NumericNode("12px"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".logo")),
				new DeclarationNode(
					"width",
					new NumericNode("300px"))),
			new RuleSetNode(
				new SelectorNode(
					new ValueNode("#header"),
					new ValueNode(".logo:hover")),
				new DeclarationNode(
					"text-decoration",
					new ValueNode("none"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessMultipleNestedRulesTest() throws IOException {

		CssToken[] input = {
				CssToken.value("#header"),
				CssToken.value(".foo1"),
				CssToken.operator(","),
				CssToken.value("#footer"),
				CssToken.value(".foo2"),
				CssToken.blockBegin(),

				CssToken.value(".bar"),
				CssToken.value("em"),
				CssToken.operator(","),
				CssToken.value("h3"),
				CssToken.value(".baz"),
				CssToken.blockBegin(),
				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("red"),
				CssToken.ruleDelim(),
				CssToken.blockEnd(),

				CssToken.value("color"),
				CssToken.operator(":"),
				CssToken.color("blue"),
				CssToken.ruleDelim(),

				CssToken.blockEnd()
			};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode[] {
					new SelectorNode(
						new ValueNode("#header"),
						new ValueNode(".foo1")),
					new SelectorNode(
						new ValueNode("#footer"),
						new ValueNode(".foo2"))
				},
				new DeclarationNode(
					"color",
					new ColorNode("blue"))),
			new RuleSetNode(
				new SelectorNode[] {
					new SelectorNode(
						new ValueNode("#header"),
						new ValueNode(".foo1"),
						new ValueNode(".bar"),
						new ValueNode("em")),
					new SelectorNode(
						new ValueNode("#footer"),
						new ValueNode(".foo2"),
						new ValueNode(".bar"),
						new ValueNode("em")),
					new SelectorNode(
						new ValueNode("#header"),
						new ValueNode(".foo1"),
						new ValueNode("h3"),
						new ValueNode(".baz")),
					new SelectorNode(
						new ValueNode("#footer"),
						new ValueNode(".foo2"),
						new ValueNode("h3"),
						new ValueNode(".baz"))
				},
				new DeclarationNode(
					"color",
					new ColorNode("red"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}

	@Test
	public void lessMixinFunctionTest() throws IOException {

		CssToken[] input = {
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

			CssToken.blockEnd()
		};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(".rounded_corners"),
				new DeclarationNode(
					"-moz-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"-webkit-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"border-radius",
					new NumericNode("5px"))));

		StyleSheetNode actual = new CssParser(Syntax.LESS).parse(input);

		assertEquals(expected, actual);
	}

	@Ignore("Mixins are not ready yet")
	@Test
	public void lessMixinUsageTest() throws IOException {

		CssToken[] input = {
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
			CssToken.value("color"),
			CssToken.operator(":"),
			CssToken.color("red"),
			CssToken.ruleDelim(),
			CssToken.blockEnd(),

			CssToken.value("#footer"),
			CssToken.blockBegin(),
			CssToken.func(".rounded_corners"),
			CssToken.numeric("10px"),
			CssToken.operator(")"),
			CssToken.ruleDelim(),
			CssToken.value("color"),
			CssToken.operator(":"),
			CssToken.color("blue"),
			CssToken.ruleDelim(),
			CssToken.blockEnd()
		};

		StyleSheetNode expected = new StyleSheetNode(
			new RuleSetNode(
				new SelectorNode(".rounded_corners"),
				new DeclarationNode(
					"-moz-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"-webkit-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"border-radius",
					new NumericNode("5px"))),
			new RuleSetNode(
				new SelectorNode("#header"),
				new DeclarationNode(
					"-moz-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"-webkit-border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"border-radius",
					new NumericNode("5px")),
				new DeclarationNode(
					"color",
					new ColorNode("red"))),
			new RuleSetNode(
				new SelectorNode("#footer"),
				new DeclarationNode(
					"-moz-border-radius",
					new NumericNode("10px")),
				new DeclarationNode(
					"-webkit-border-radius",
					new NumericNode("10px")),
				new DeclarationNode(
					"border-radius",
					new NumericNode("10px")),
				new DeclarationNode(
					"color",
					new ColorNode("blue"))));

		StyleSheetNode actual = new CssParser().parse(input);

		assertEquals(expected, actual);
	}
}
