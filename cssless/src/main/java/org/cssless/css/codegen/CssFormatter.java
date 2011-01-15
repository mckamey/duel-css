package org.cssless.css.codegen;

import java.io.IOException;
import org.cssless.css.ast.*;

/**
 * Generates CSS stylesheet source 
 * Inherently thread-safe as contains no mutable instance data.
 */
public class CssFormatter {

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

		// TODO: expose another setting for spacing?
		int spacing = this.settings.useInlineBraces() ? 1 : 2; 
		boolean needsDelim = false;
		for (CssNode node : stylesheet.getChildren()) {
			if (needsDelim) {
				this.writeln(output, 0, spacing);
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

		} else if (node instanceof FunctionNode) {
			this.writeFunction(output, (FunctionNode)node, 0);

		} else if (node instanceof AccessorNode) {
			this.writeAccessor(output, (AccessorNode)node, 0);

		} else if (node instanceof MultiValueNode) {
			this.writeContainer(output, ((MultiValueNode)node).getContainer(), 0);

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

		} else if (node instanceof FunctionNode) {
			this.writeFunction(output, (FunctionNode)node, depth);

		} else if (node instanceof AccessorNode) {
			this.writeAccessor(output, (AccessorNode)node, depth);

		} else if (node instanceof MultiValueNode) {
			this.writeContainer(output, ((MultiValueNode)node).getContainer(), depth);

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

		// remove empty rule-sets in compact mode
		if (!this.prettyPrint && !node.hasChildren()) {
			return;
		}
		
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
		if (node.isImportant()) {
			if (this.prettyPrint) {
				output.append(' ');
			}
			output.append("!important");
		}
		output.append(';');
	}

	private void writeExpression(Appendable output, ContainerNode node, int depth)
		throws IOException {

		WordBreak prev = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				prev = this.writeWordBreak(output, prev, child.getWordBreak(this.prettyPrint));
				this.writeNode(output, child, depth);
			}
		}
	}

	private void writeBlock(Appendable output, BlockNode node, int depth)
		throws IOException {

		output.append('{');
		depth++;

		// TODO: expose another setting for spacing?
		if (this.settings.useInlineBraces()) {
			for (CssNode child : node.getChildren()) {
				this.writeln(output, depth);
				this.writeNode(output, child, depth);
			}
		} else {
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
		}

		this.writeln(output, --depth);
		output.append('}');
	}

	private void writeFunction(Appendable output, FunctionNode node, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('(');
		this.writeContainer(output, node.getContainer(), depth);
		output.append(')');
	}

	private void writeAccessor(Appendable output, AccessorNode node, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('[');
		this.writeContainer(output, node.getContainer(), depth);
		output.append(']');
	}

	private void writeContainer(Appendable output, ContainerNode node, int depth)
		throws IOException {

		WordBreak prev = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				prev = this.writeWordBreak(output, prev, child.getWordBreak(this.prettyPrint));
				this.writeNode(output, child, depth);
			}
		}
	}

	private void writeValue(Appendable output, ValueNode node)
		throws IOException {

		output.append(node.getValue(!this.prettyPrint));
	}

	private void writeComment(Appendable output, CommentNode node, int depth)
		throws IOException {

		if (this.prettyPrint) {
			output.append("/*").append(node.getValue()).append("*/");
		}
	}

	private WordBreak writeWordBreak(Appendable output, WordBreak prev, WordBreak next)
		throws IOException {

		if (prev != null &&
			(WordBreak.BOTH.equals(prev) || WordBreak.POST.equals(prev)) &&
			(WordBreak.BOTH.equals(next) || WordBreak.PRE.equals(next))) {

			// emit significant whitespace
			output.append(' ');
		}
		return next;
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
}
