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

	public CssFormatter(CodeGenSettings codeGenSettings) {
		settings = (codeGenSettings != null) ? codeGenSettings : new CodeGenSettings();
		prettyPrint = (!settings.getIndent().isEmpty() || !settings.getNewline().isEmpty());
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

		write(output, stylesheet, null);
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
		int spacing = settings.useInlineBraces() ? 1 : 2; 
		boolean needsDelim = false;
		for (CssNode node : stylesheet.getChildren()) {
			if (needsDelim) {
				writeln(output, 0, spacing);
			} else {
				needsDelim = true;
			}
			writeNode(output, node, filter, 0);
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
			writeAtRule(output, (AtRuleNode)node, filter, 0);

		} else if (node instanceof RuleSetNode) {
			writeRuleSet(output, (RuleSetNode)node, filter, 0);

		} else if (node instanceof DeclarationNode) {
			writeDeclaration(output, (DeclarationNode)node, filter, 0);

		} else if (node instanceof CommentNode) {
			writeComment(output, (CommentNode)node, filter, 0);

		} else if (node instanceof FunctionNode) {
			writeFunction(output, (FunctionNode)node, filter, 0);

		} else if (node instanceof AccessorNode) {
			writeAccessor(output, (AccessorNode)node, filter, 0);

		} else if (node instanceof MultiValueNode) {
			writeContainer(output, ((MultiValueNode)node).getContainer(), filter, 0);

		} else if (node instanceof ValueNode) {
			writeValue(output, (ValueNode)node);

		} else if (node instanceof BlockNode) {
			writeBlock(output, (BlockNode)node, filter, 0);

		} else if (node instanceof ContainerNode) {
			writeExpression(output, (ContainerNode)node, filter, 0);
			
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
			writeAtRule(output, (AtRuleNode)node, filter, depth);

		} else if (node instanceof RuleSetNode) {
			writeRuleSet(output, (RuleSetNode)node, filter, depth);

		} else if (node instanceof DeclarationNode) {
			writeDeclaration(output, (DeclarationNode)node, filter, depth);

		} else if (node instanceof CommentNode) {
			writeComment(output, (CommentNode)node, filter, depth);

		} else if (node instanceof FunctionNode) {
			writeFunction(output, (FunctionNode)node, filter, depth);

		} else if (node instanceof AccessorNode) {
			writeAccessor(output, (AccessorNode)node, filter, depth);

		} else if (node instanceof MultiValueNode) {
			writeContainer(output, ((MultiValueNode)node).getContainer(), filter, depth);

		} else if (node instanceof ValueNode) {
			writeValue(output, (ValueNode)node);

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
			writeExpression(output, node, filter, depth);
		}

		BlockNode block = node.getBlock();
		if (block != null) {
			if (prettyPrint) {
				if (settings.useInlineBraces()) {
					output.append(' ');
				} else {
					writeln(output, depth);
				}
			}
			writeBlock(output, block, filter, depth);
		} else {
			output.append(';');
		}
	}

	private void writeRuleSet(Appendable output, RuleSetNode node, CssFilter filter, int depth)
		throws IOException {

		// remove empty rule-sets in compact mode
		if (!prettyPrint && !node.hasChildren()) {
			return;
		}
		
		boolean needsDelim = false;
		for (SelectorNode selector : node.getSelectors()) {
			if (needsDelim) {
				output.append(',');
				if (prettyPrint) {
					output.append(' ');
				}
			} else {
				needsDelim = true;
			}
			writeExpression(output, selector, filter, depth);
		}

		if (prettyPrint) {
			if (settings.useInlineBraces()) {
				output.append(' ');
			} else {
				writeln(output, depth);
			}
		}
		writeBlock(output, node, filter, depth);
	}

	private void writeDeclaration(Appendable output, DeclarationNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getIdent());
		output.append(':');
		if (prettyPrint) {
			output.append(' ');
		}
		writeExpression(output, node, filter, depth);
		if (node.isImportant()) {
			if (prettyPrint) {
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
				prev = writeWordBreak(output, prev, child);
				writeNode(output, child, filter, depth);
			}
		}
	}

	private void writeBlock(Appendable output, BlockNode node, CssFilter filter, int depth)
		throws IOException {

		output.append('{');
		depth++;

		// TODO: expose another setting for spacing?
		if (settings.useInlineBraces()) {
			for (CssNode child : node.getChildren()) {
				writeln(output, depth);
				writeNode(output, child, filter, depth);
			}
		} else {
			boolean needsDelim = false;
			for (CssNode child : node.getChildren()) {
				if (needsDelim && !(child instanceof DeclarationNode) && !(child instanceof CommentNode)) {
					writeln(output, depth, 2);
				} else {
					needsDelim = true;
					writeln(output, depth);
				}
				writeNode(output, child, filter, depth);
			}
		}

		writeln(output, --depth);
		output.append('}');
	}

	private void writeFunction(Appendable output, FunctionNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('(');
		writeContainer(output, node.getContainer(), filter, depth);
		output.append(')');
	}

	private void writeAccessor(Appendable output, AccessorNode node, CssFilter filter, int depth)
		throws IOException {

		output.append(node.getValue());
		output.append('[');
		writeContainer(output, node.getContainer(), filter, depth);
		output.append(']');
	}

	private void writeContainer(Appendable output, ContainerNode node, CssFilter filter, int depth)
		throws IOException {

		WordBreak prev = null;
		if (node.hasChildren()) {
			for (CssNode child : node.getChildren()) {
				prev = writeWordBreak(output, prev, child);
				writeNode(output, child, filter, depth);
			}
		}
	}

	private void writeValue(Appendable output, ValueNode node)
		throws IOException {

		output.append(node.getValue(!prettyPrint));
	}

	private void writeComment(Appendable output, CommentNode node, CssFilter filter, int depth)
		throws IOException {

		if (prettyPrint) {
			output.append("/*").append(node.getValue()).append("*/");
		}
	}

	private WordBreak writeWordBreak(Appendable output, WordBreak prev, CssNode child)
		throws IOException {

		if (!prettyPrint && (child instanceof CommentNode)) {
			// non-printed comments interfere with word breaks
			return prev;
		}

		WordBreak next = child.getWordBreak(prettyPrint);
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

		writeln(output, depth, 1);
	}
	
	private void writeln(Appendable output, int depth, int lines)
		throws IOException {

		String newline = settings.getNewline();
		for (int i=lines; i>0; i--) {
			output.append(newline);
		}

		String indent = settings.getIndent();
		for (int i=depth; i>0; i--) {
			output.append(indent);
		}
	}
}
