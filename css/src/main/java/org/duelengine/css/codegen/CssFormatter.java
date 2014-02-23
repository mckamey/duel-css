package org.duelengine.css.codegen;

import java.io.IOException;

import org.duelengine.css.ast.AccessorNode;
import org.duelengine.css.ast.AtRuleNode;
import org.duelengine.css.ast.BlockNode;
import org.duelengine.css.ast.CommentNode;
import org.duelengine.css.ast.ContainerNode;
import org.duelengine.css.ast.CssNode;
import org.duelengine.css.ast.DeclarationNode;
import org.duelengine.css.ast.FunctionNode;
import org.duelengine.css.ast.MultiValueNode;
import org.duelengine.css.ast.RuleSetNode;
import org.duelengine.css.ast.SelectorNode;
import org.duelengine.css.ast.StyleSheetNode;
import org.duelengine.css.ast.ValueNode;
import org.duelengine.css.ast.WordBreak;

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

	public static String getFileExtension() {
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

		this.write(output, stylesheet, null);
	}

	/**
	 * Generates the text for the stylesheet 
	 * @param output
	 * @param stylesheet
	 * @throws IOException
	 */
	public void write(Appendable output, StyleSheetNode stylesheet, CssFilter filter)
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
			this.writeNode(output, node, filter, 0);
		}
	}

	/**
	 * Used for debugging and .toString()
	 * @param output
	 * @param node
	 * @throws IOException 
	 */
	public void writeNode(Appendable output, CssNode node, CssFilter filter)
		throws IOException {

		if (filter != null) {
			node = filter.filter(node);
		}
		
		if (node == null) {
			output.append("null");

		} else if (node instanceof AtRuleNode) {
			this.writeAtRule(output, (AtRuleNode)node, filter, 0);

		} else if (node instanceof RuleSetNode) {
			this.writeRuleSet(output, (RuleSetNode)node, filter, 0);

		} else if (node instanceof DeclarationNode) {
			this.writeDeclaration(output, (DeclarationNode)node, filter, 0);

		} else if (node instanceof CommentNode) {
			this.writeComment(output, (CommentNode)node, filter, 0);

		} else if (node instanceof FunctionNode) {
			this.writeFunction(output, (FunctionNode)node, filter, 0);

		} else if (node instanceof AccessorNode) {
			this.writeAccessor(output, (AccessorNode)node, filter, 0);

		} else if (node instanceof MultiValueNode) {
			this.writeContainer(output, ((MultiValueNode)node).getContainer(), filter, 0);

		} else if (node instanceof ValueNode) {
			this.writeValue(output, (ValueNode)node);

		} else if (node instanceof BlockNode) {
			this.writeBlock(output, (BlockNode)node, filter, 0);

		} else if (node instanceof ContainerNode) {
			this.writeExpression(output, (ContainerNode)node, filter, 0);
			
		} else if (node != null) {
			throw new UnsupportedOperationException("Node not yet implemented: "+node.getClass());
		}
	}

	private void writeNode(Appendable output, CssNode node, CssFilter filter, int depth)
		throws IOException {

		if (filter != null) {
			node = filter.filter(node);
		}

		if (node instanceof AtRuleNode) {
			this.writeAtRule(output, (AtRuleNode)node, filter, depth);

		} else if (node instanceof RuleSetNode) {
			this.writeRuleSet(output, (RuleSetNode)node, filter, depth);

		} else if (node instanceof DeclarationNode) {
			this.writeDeclaration(output, (DeclarationNode)node, filter, depth);

		} else if (node instanceof CommentNode) {
			this.writeComment(output, (CommentNode)node, filter, depth);

		} else if (node instanceof FunctionNode) {
			this.writeFunction(output, (FunctionNode)node, filter, depth);

		} else if (node instanceof AccessorNode) {
			this.writeAccessor(output, (AccessorNode)node, filter, depth);

		} else if (node instanceof MultiValueNode) {
			this.writeContainer(output, ((MultiValueNode)node).getContainer(), filter, depth);

		} else if (node instanceof ValueNode) {
			this.writeValue(output, (ValueNode)node);

		} else if (node != null) {
			throw new UnsupportedOperationException("Node not yet implemented: "+node.getClass());
		}
	}

	private void writeAtRule(Appendable output, AtRuleNode node, CssFilter filter, int depth)
		throws IOException {

		output.append('@');
		output.append(node.getKeyword());

		if (node.hasChildren()) {
			output.append(' ');
			this.writeExpression(output, node, filter, depth);
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
			this.writeBlock(output, block, filter, depth);
		} else {
			output.append(';');
		}
	}

	private void writeRuleSet(Appendable output, RuleSetNode node, CssFilter filter, int depth)
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
			this.writeExpression(output, selector, filter, depth);
		}

		if (this.prettyPrint) {
			if (this.settings.useInlineBraces()) {
				output.append(' ');
			} else {
				this.writeln(output, depth);
			}
		}
		this.writeBlock(output, node, filter, depth);
	}

	private void writeDeclaration(Appendable output, DeclarationNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getIdent());
		output.append(':');
		if (this.prettyPrint) {
			output.append(' ');
		}
		this.writeExpression(output, node, filter, depth);
		if (node.isImportant()) {
			if (this.prettyPrint) {
				output.append(' ');
			}
			output.append("!important");
		}
		output.append(';');
	}

	private void writeExpression(Appendable output, ContainerNode node, CssFilter filter, int depth)
		throws IOException {

		WordBreak prev = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				prev = this.writeWordBreak(output, prev, child);
				this.writeNode(output, child, filter, depth);
			}
		}
	}

	private void writeBlock(Appendable output, BlockNode node, CssFilter filter, int depth)
		throws IOException {

		output.append('{');
		depth++;

		// TODO: expose another setting for spacing?
		if (this.settings.useInlineBraces()) {
			for (CssNode child : node.getChildren()) {
				this.writeln(output, depth);
				this.writeNode(output, child, filter, depth);
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
				this.writeNode(output, child, filter, depth);
			}
		}

		this.writeln(output, --depth);
		output.append('}');
	}

	private void writeFunction(Appendable output, FunctionNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('(');
		this.writeContainer(output, node.getContainer(), filter, depth);
		output.append(')');
	}

	private void writeAccessor(Appendable output, AccessorNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('[');
		this.writeContainer(output, node.getContainer(), filter, depth);
		output.append(']');
	}

	private void writeContainer(Appendable output, ContainerNode node, CssFilter filter, int depth)
		throws IOException {

		WordBreak prev = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				prev = this.writeWordBreak(output, prev, child);
				this.writeNode(output, child, filter, depth);
			}
		}
	}

	private void writeValue(Appendable output, ValueNode node)
		throws IOException {

		output.append(node.getValue(!this.prettyPrint));
	}

	private void writeComment(Appendable output, CommentNode node, CssFilter filter, int depth)
		throws IOException {

		if (this.prettyPrint) {
			output.append("/*").append(node.getValue()).append("*/");
		}
	}

	private WordBreak writeWordBreak(Appendable output, WordBreak prev, CssNode child)
		throws IOException {

		if (!this.prettyPrint && (child instanceof CommentNode)) {
			// non-printed comments interfere with word breaks
			return prev;
		}

		WordBreak next = child.getWordBreak(this.prettyPrint);
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
