package org.cssless.css.codegen;

import java.io.IOException;
import org.cssless.css.ast.*;

/**
 * Generates CSS stylesheet source 
 * Inherently thread-safe as contains no mutable instance data.
 */
public class CssFormatter {

	private enum WordBreak
	{
		NONE,
		PRE,
		POST,
		BOTH
	};
	
	private final CodeGenSettings settings;
	private final boolean prettyPrint;

	public CssFormatter() {
		this(null);
	}

	public CssFormatter(CodeGenSettings settings) {
		this.settings = (settings != null) ? settings : new CodeGenSettings();
		this.prettyPrint = (!this.settings.getIndent().isEmpty() || !this.settings.getNewline().isEmpty());
	}

	public String getFileExtension() {
		return ".css";
	}

	/**
	 * Generates the text for the stylesheet 
	 * @param output
	 * @param stylesheet
	 * @throws IOException
	 */
	public void write(Appendable output, StyleSheetNode stylesheet)
		throws IOException {

		if (output == null) {
			throw new NullPointerException("output");
		}
		if (stylesheet == null) {
			throw new NullPointerException("stylesheet");
		}

		boolean needsDelim = false;
		for (CssNode node : stylesheet.getChildren()) {
			if (needsDelim) {
				this.writeln(output, 0, 2);
			} else {
				needsDelim = true;
			}
			this.writeNode(output, node, 0);
		}
	}

	/**
	 * Used for debugging and .toString()
	 * @param output
	 * @param node
	 * @throws IOException 
	 */
	public void writeNode(Appendable output, CssNode node)
		throws IOException {

		if (node == null) {
			output.append("null");

		} else if (node instanceof AtRuleNode) {
			this.writeAtRule(output, (AtRuleNode)node, 0);

		} else if (node instanceof RuleSetNode) {
			this.writeRuleSet(output, (RuleSetNode)node, 0);

		} else if (node instanceof DeclarationNode) {
			this.writeDeclaration(output, (DeclarationNode)node, 0);

		} else if (node instanceof CommentNode) {
			this.writeComment(output, (CommentNode)node, 0);

		} else if (node instanceof ValueNode) {
			this.writeValue(output, (ValueNode)node);

		} else if (node instanceof BlockNode) {
			this.writeBlock(output, (BlockNode)node, 0);

		} else if (node instanceof ContainerNode) {
			this.writeExpression(output, (ContainerNode)node, 0);
			
		} else if (node != null) {
			throw new UnsupportedOperationException("Node not yet implemented: "+node.getClass());
		}
	}
	
	private void writeNode(Appendable output, CssNode node, int depth)
		throws IOException {

		if (node instanceof AtRuleNode) {
			this.writeAtRule(output, (AtRuleNode)node, depth);

		} else if (node instanceof RuleSetNode) {
			this.writeRuleSet(output, (RuleSetNode)node, depth);

		} else if (node instanceof DeclarationNode) {
			this.writeDeclaration(output, (DeclarationNode)node, depth);

		} else if (node instanceof CommentNode) {
			this.writeComment(output, (CommentNode)node, depth);

		} else if (node instanceof ValueNode) {
			this.writeValue(output, (ValueNode)node);

		} else if (node != null) {
			throw new UnsupportedOperationException("Node not yet implemented: "+node.getClass());
		}
	}

	private void writeAtRule(Appendable output, AtRuleNode node, int depth)
		throws IOException {

		output.append('@');
		output.append(node.getKeyword());

		if (node.hasChildren()) {
			output.append(' ');
			this.writeExpression(output, node, depth);
		}

		BlockNode block = node.getBlock();
		if (block != null) {
			if (this.prettyPrint) {
				if (this.settings.useInlineBraces()) {
					output.append(' ');
				} else {
					this.writeln(output, depth);
				}
			}
			this.writeBlock(output, block, depth);
		} else {
			output.append(';');
		}
	}

	private void writeRuleSet(Appendable output, RuleSetNode node, int depth)
		throws IOException {

		boolean needsDelim = false;
		for (SelectorNode selector : node.getSelectors()) {
			if (needsDelim) {
				output.append(',');
				if (this.prettyPrint) {
					output.append(' ');
				}
			} else {
				needsDelim = true;
			}
			this.writeExpression(output, selector, depth);
		}

		if (this.prettyPrint) {
			if (this.settings.useInlineBraces()) {
				output.append(' ');
			} else {
				this.writeln(output, depth);
			}
		}
		this.writeBlock(output, node, depth);
	}

	private void writeDeclaration(Appendable output, DeclarationNode node, int depth)
		throws IOException {

		output.append(node.getIdent());
		output.append(':');
		if (this.prettyPrint) {
			output.append(' ');
		}
		this.writeExpression(output, node, depth);
		output.append(';');
	}

	private void writeExpression(Appendable output, ContainerNode node, int depth)
		throws IOException {

		WordBreak last = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				// check for significant whitespace
				WordBreak next = this.getWordBreak(child);
				if (last != null &&
					(WordBreak.BOTH.equals(last) || WordBreak.POST.equals(last)) &&
					(WordBreak.BOTH.equals(next) || WordBreak.PRE.equals(next))) {

					output.append(' ');
				}
				last = next;

				this.writeNode(output, child, depth);
			}
		}
	}

	private void writeBlock(Appendable output, BlockNode node, int depth)
		throws IOException {

		output.append('{');
		depth++;

		boolean needsDelim = false;
		for (CssNode child : node.getChildren()) {
			if (needsDelim && !(child instanceof DeclarationNode) && !(child instanceof CommentNode)) {
				this.writeln(output, depth, 2);
			} else {
				needsDelim = true;
				this.writeln(output, depth);
			}
			this.writeNode(output, child, depth);
		}

		this.writeln(output, --depth);
		output.append('}');
	}

	private void writeValue(Appendable output, ValueNode node)
		throws IOException {

		output.append(node.getValue());
	}

	private void writeComment(Appendable output, CommentNode node, int depth)
		throws IOException {

		if (this.prettyPrint) {
			output.append("/*").append(node.getValue()).append("*/");
		}
	}

	private void writeln(Appendable output, int depth)
		throws IOException {

		this.writeln(output, depth, 1);
	}
	
	private void writeln(Appendable output, int depth, int lines)
		throws IOException {

		String newline = this.settings.getNewline();
		for (int i=lines; i>0; i--) {
			output.append(newline);
		}

		String indent = this.settings.getIndent();
		for (int i=depth; i>0; i--) {
			output.append(indent);
		}
	}

	private WordBreak getWordBreak(CssNode node) {
		if (node == null) {
			return WordBreak.NONE;

		} else if (node instanceof CombinatorNode || node instanceof StringNode) {
			return this.prettyPrint ? WordBreak.BOTH : WordBreak.NONE;

		} else if (node instanceof OperatorNode) {
			String value = ((OperatorNode)node).getValue();
			char end = (value != null && value.length() == 1) ? value.charAt(0) : '\0'; 
			if (end == ',' || end == ')' || end == ']') {
				return WordBreak.POST;
			}

			return WordBreak.NONE;

		} else if (node instanceof ValueNode) {
			String value = ((ValueNode)node).getValue();
			char end = (value != null && !value.isEmpty()) ? value.charAt(value.length()-1) : '\0'; 
			if (end == '(' || end == '[') {
				return WordBreak.PRE;
			}
			return WordBreak.BOTH;

		} else {
			return WordBreak.NONE;
		}
	}
}
