package org.duelengine.css.parsing;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.duelengine.css.ast.AccessorNode;
import org.duelengine.css.ast.AtRuleNode;
import org.duelengine.css.ast.BlockNode;
import org.duelengine.css.ast.ColorNode;
import org.duelengine.css.ast.CombinatorNode;
import org.duelengine.css.ast.CombinatorType;
import org.duelengine.css.ast.CommentNode;
import org.duelengine.css.ast.ContainerNode;
import org.duelengine.css.ast.CssNode;
import org.duelengine.css.ast.DeclarationNode;
import org.duelengine.css.ast.FunctionNode;
import org.duelengine.css.ast.LessVariableDeclarationNode;
import org.duelengine.css.ast.LessVariableReferenceNode;
import org.duelengine.css.ast.NumericNode;
import org.duelengine.css.ast.OperatorNode;
import org.duelengine.css.ast.RuleSetNode;
import org.duelengine.css.ast.SelectorNode;
import org.duelengine.css.ast.StringNode;
import org.duelengine.css.ast.StyleSheetNode;
import org.duelengine.css.ast.ValueNode;
import org.duelengine.css.codegen.ArithmeticEvaluator;

/**
 * Processes a token sequence into AST
 */
public class CssParser {

	private static final String HSL = "hsl";
	private static final String HSLA = "hsla";

	private enum NODE_CONTEXT {
		ACCESSOR,
		AT_RULE,
		DECLARATION,
		FUNCTION,
		SELECTOR
	}
	
	private CssToken next;
	private Iterator<CssToken> tokens;

	/**
	 * Parses token sequence into AST
	 * @param tokens
	 * @return
	 * @throws IOException 
	 */
	public StyleSheetNode parse(CssToken... tokens)
		throws IOException {

		return parse(tokens != null ? Arrays.asList(tokens).iterator() : null);
	}

	/**
	 * Parses token sequence into AST
	 * @param tokens
	 * @return
	 * @throws IOException 
	 */
	public StyleSheetNode parse(Iterable<CssToken> tokens)
		throws IOException {

		return parse(tokens != null ? tokens.iterator() : null);
	}

	/**
	 * Parses token sequence into AST
	 * @param cssTokens
	 * @return
	 */
	public StyleSheetNode parse(Iterator<CssToken> cssTokens)
		throws IOException {

		if (cssTokens == null) {
			throw new NullPointerException("cssTokens");
		}

		tokens = cssTokens;
		try {
			StyleSheetNode document = new StyleSheetNode(0, 0, 0);
			while (hasNext()) {
				parseStatement(document, false);
			}
			return document;

		} finally {
			tokens = null;
			next = null;
		}
	}

	/**
	 * Processes the next node
	 * @param parent
	 * @throws Exception
	 */
	private void parseStatement(ContainerNode parent, boolean isRuleSet)
		throws IOException {

		// to support LESS, blocks can be nested
		// - vars
		// - mixins
		// - declarations (unless root)
		// - statements

		switch (next.getToken()) {
			case AT_RULE:
				if (isRuleSet) {
					throw new InvalidTokenException("Invalid token inside rule-set: "+next, next);
				}

				// LESS also uses this for scoped variables
				parseAtRule(parent);
				break;

			case RULE_DELIM:
				// consume extraneous ';'
				next = null;
				break;

			case FUNCTION:
			case ACCESSOR:
			case STRING:
			case NUMERIC:
			case COLOR:
			case VALUE:
			case OPERATOR:
				// LESS can have nested rule-sets
				parseRuleSet(parent, isRuleSet);
				break;

			case COMMENT:
				parent.appendChild(new CommentNode(next.getValue(), next.getIndex(), next.getLine(), next.getColumn()));
				// consume token
				next = null;
				break;

			case ERROR:
				throw throwErrorToken(next);

			default:
				throw new InvalidTokenException("Invalid token: "+next, next);
		}
	}

	private void parseAtRule(ContainerNode parent)
		throws IOException {

		String keyword = next.getValue();
		if (!CssGrammar.isAtRuleKeyword(keyword)) {
			CssToken ident = next;
			next = null;
			parseDeclaration(parent, ident);
			return;
		}

		AtRuleNode atRule = new AtRuleNode(keyword, next.getIndex(), next.getLine(), next.getColumn());
		parent.appendChild(atRule);
		// consume at-rule
		next = null;

		while (hasNext()) {
			switch (next.getToken()) {
				case BLOCK_BEGIN:
					BlockNode block = new BlockNode(next.getIndex(), next.getLine(), next.getColumn());
					atRule.setBlock(block);
					String canonicalKeyword = CssGrammar.removeVendorPrefix(atRule.getKeyword());
					boolean asRuleSet = !("media".equals(canonicalKeyword) || "keyframes".equals(canonicalKeyword));
					parseBlock(block, asRuleSet);
					return;

				case RULE_DELIM:
					// consume token
					next = null;
					return;

				default:
					parseValue(atRule, next, false, NODE_CONTEXT.AT_RULE);
					continue;
			}
		}
	}

	private void parseRuleSet(ContainerNode parent, boolean nested)
		throws IOException {

		// consume ident
		CssToken ident = next;
		next = null;

		// if nested, then must be rule set
		if (nested && hasNext() && CssTokenType.OPERATOR.equals(next.getToken()) && ":".equals(next.getValue())) {
			parseDeclaration(parent, ident);
			return;
		}

		RuleSetNode ruleSet = new RuleSetNode(ident.getIndex(), ident.getLine(), ident.getColumn());
		parent.appendChild(ruleSet);

		RuleSetNode nestedParent = (parent instanceof RuleSetNode) ? (RuleSetNode)parent : null;

		if (parseSelector(ruleSet, ident)) {
			if (!nested) {
				throw new InvalidTokenException("Invalid sequence in rule-set: "+ident, ident);
			}

			// not a selector but a mixin
			evalMixins(nestedParent, ruleSet);
			return;
		}

		while (hasNext()) {
			switch (next.getToken()) {
				case BLOCK_BEGIN:
					if (nestedParent != null) {
						// LESS allows nested rules, unroll selectors here
						ruleSet.expandSelectors(nestedParent.getSelectors());
					}
					parseBlock(ruleSet, true);
					return;

				case ACCESSOR:
				case FUNCTION:
				case NUMERIC:
				case COLOR:
				case STRING:
				case VALUE:
				case OPERATOR:
					if (parseSelector(ruleSet, next)){
						if (!nested) {
							throw new InvalidTokenException("Invalid sequence in rule-set: "+ident, ident);
						}

						// not a selector but a mixin
						evalMixins(nestedParent, ruleSet);
						return;
					}
					continue;

				case COMMENT:
					ruleSet.appendChild(new CommentNode(next.getValue(), next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				case ERROR:
					throw throwErrorToken(next);

				case BLOCK_END:
					if (!nested) {
						throw new InvalidTokenException("Invalid token in rule-set: "+next, next);
					}

					// allow parent to consume token
					return;

				default:
					throw new InvalidTokenException("Invalid token in rule-set: "+next, next);
			}
		}
	}

	private boolean parseSelector(RuleSetNode ruleSet, CssToken start)
		throws IOException {

		SelectorNode selector = new SelectorNode(start.getIndex(), start.getLine(), start.getColumn());
		ruleSet.addSelector(selector);

		int nesting = 0;

		// check identity of start
		if (start != next) {
			// need to process start token outside of loop due to look ahead needed for LESS nested rule-sets
			switch (start.getToken()) {
				case OPERATOR:
					String value = start.getValue();
					if (value != null) {
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								nesting--;
								break;
						}
					}

					selector.appendChild(new OperatorNode(value, start.getIndex(), start.getLine(), start.getColumn()));
					break;

				default:
					parseValue(selector, start, true, NODE_CONTEXT.SELECTOR);
					break;
			}
		}

		while (hasNext()) {
			switch (next.getToken()) {
				case BLOCK_BEGIN:
					// terminate selector
					return false;

				case BLOCK_END:
					// signal was mixin
					return true;

				case RULE_DELIM:
					// consume delim
					next = null;
					// signal was mixin
					return true;

				case OPERATOR:
					String value = next.getValue();
					if (value != null) {
						if (nesting <= 0) {
							if (",".equals(value)) {
								// consume delim
								next = null;
								// terminate selector
								return false;
							}

							CombinatorType combinator = CombinatorNode.getCombinator(value);
							if (combinator != null) {
								selector.appendChild(new CombinatorNode(combinator, next.getIndex(), next.getLine(), next.getColumn()));
								// consume token
								next = null;
								continue;
							}
						}

						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								nesting--;
								break;
						}
					}

					selector.appendChild(new OperatorNode(value, next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				default:
					parseValue(selector, next, true, NODE_CONTEXT.SELECTOR);
					continue;
			}
		}

		return false;
	}

	private void parseDeclaration(ContainerNode parent, CssToken ident) {

		if (!hasNext() || !CssTokenType.OPERATOR.equals(next.getToken()) || !":".equals(next.getValue())) {
			throw new InvalidTokenException("Invalid declaration: "+ident, ident);
		}
		// consume ':'
		next = null;

		boolean requiredEval = (ident.getToken() == CssTokenType.AT_RULE);
		boolean optionalEval = false;

		// LESS variable declarations leverage @rule syntax
		DeclarationNode declaration = requiredEval ?
			new LessVariableDeclarationNode(ident.getValue(), ident.getIndex(), ident.getLine(), ident.getColumn()) :
			new DeclarationNode(ident.getValue(), ident.getIndex(), ident.getLine(), ident.getColumn());

		parent.appendChild(declaration);

		int nesting = 0;

		while (hasNext()) {
			switch (next.getToken()) {
				case BLOCK_END:
					if (requiredEval || optionalEval) {
						evalExpressions(declaration, requiredEval);
					}
					return;

				case AT_RULE:
					// LESS variable references leverage @rule syntax
					declaration.appendChild(new LessVariableReferenceNode(next.getValue(), next.getIndex(), next.getLine(), next.getColumn()));
					requiredEval = true;
					// consume token
					next = null;
					continue;

				case RULE_DELIM:
					if (nesting <= 0) {
						// consume ';' as end of declaration
						next = null;
						if (requiredEval || optionalEval) {
							evalExpressions(declaration, requiredEval);
						}
						return;
					}
					// still within function
					declaration.appendChild(new OperatorNode(";", next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				case OPERATOR:
					String value = next.getValue();
					if (value != null) {
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								nesting--;
								if (nesting < 0) {
									// end of LESS mixin function param, let caller consume token
									if (requiredEval || optionalEval) {
										evalExpressions(declaration, requiredEval);
									}
									return;
								}
								break;
//							case ',':
//								if (nesting <= 0) {
//									// consume token
//									next = null;
//									// end of LESS mixin function param
//									if (requiredEval || optionalEval) {
//										evalExpressions(declaration, requiredEval);
//									}
//									return;
//								}
//								break;
							case '+':
							case '-':
							case '*':
							case '/':
								optionalEval = true;
								break;
						}
					}

					declaration.appendChild(new OperatorNode(value, next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				case IMPORTANT:
					declaration.setImportant(true);
					// consume token
					next = null;
					continue;

				default:
					parseValue(declaration, next, false, NODE_CONTEXT.DECLARATION);
					continue;
			}
		}

		if (requiredEval || optionalEval) {
			evalExpressions(declaration, requiredEval);
		}
	}

	private FunctionNode parseFunction(ContainerNode parent, CssToken start, boolean isSelector) {

		FunctionNode func = new FunctionNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn());
		parent.appendChild(func);
		if (next == start) {
			// consume function start
			next = null;
		}

		String value;
		int nesting = 0;
		ContainerNode args = func.getContainer();

		while (hasNext()) {
			switch (next.getToken()) {
				case OPERATOR:
					value = next.getValue();
					if (value != null) {
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								if (nesting <= 0) {
									// elevate any params to parent scope
									if (args.hasVariables() && parent instanceof SelectorNode) {
										ContainerNode ruleSet = parent.getParent();
										if (ruleSet != null) {
											for (LessVariableDeclarationNode variable : args.getVariables()) {
												ruleSet.putVariable(variable);
											}
										}
										if (!args.hasChildren()) {
											parent.replaceChild(new ValueNode(func.getValue(), func.getIndex(), func.getLine(), func.getColumn()), func);
										}
									}

									// consume token, terminate function
									next = null;
									return func;
								}
								nesting--;
								break;
							case ',':
								// TODO: check if children are LESS vars and consume?
								break;
						}
					}

					args.appendChild(new OperatorNode(value, next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				case AT_RULE:
					// LESS mixin function param
					CssToken ident = next;
					next = null;
					parseDeclaration(args, ident);
					continue;

				default:
					value = next.getValue();
					if (value != null) {
						// should this check further into the string?
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								if (nesting <= 0) {
									// elevate any params to parent scope
									if (args.hasVariables() && parent instanceof SelectorNode) {
										ContainerNode ruleSet = parent.getParent();
										if (ruleSet != null) {
											for (LessVariableDeclarationNode variable : args.getVariables()) {
												ruleSet.putVariable(variable);
											}
										}
										if (!args.hasChildren()) {
											parent.replaceChild(new ValueNode(func.getValue(), func.getIndex(), func.getLine(), func.getColumn()), func);
										}
									}

									// trim token value, terminate function
									next = new CssToken(next.getToken(), value.substring(1), next.getIndex()+1, next.getLine(), next.getColumn()+1);
									// inject a joiner which will signal that what follows is attached to the accessor
									parent.appendChild(new CombinatorNode(CombinatorType.SELF, next.getIndex(), next.getLine(), next.getColumn()));
									return func;
								}
								nesting--;
								break;
						}
					}
					parseValue(args, next, isSelector, NODE_CONTEXT.FUNCTION);
					continue;
			}
		}

		return func;
	}

	private void parseAccessor(ContainerNode parent, CssToken start, boolean isSelector) {

		AccessorNode accessor = new AccessorNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn());
		parent.appendChild(accessor);
		if (next == start) {
			// consume function start
			next = null;
		}

		String value;
		int nesting = 0;
		ContainerNode args = accessor.getContainer();

		while (hasNext()) {
			switch (next.getToken()) {
				case OPERATOR:
					value = next.getValue();
					if (value != null) {
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								nesting--;
								break;
							case CssGrammar.OP_ATTR_END:
								if (nesting <= 0) {
									// consume token, terminate accessor
									next = null;
									return;
								}
								break;
						}
					}

					args.appendChild(new OperatorNode(value, next.getIndex(), next.getLine(), next.getColumn()));
					// consume token
					next = null;
					continue;

				default:
					value = next.getValue();
					if (value != null) {
						// should this check further into the string?
						switch (value.charAt(0)) {
							case CssGrammar.OP_PAREN_BEGIN:
								nesting++;
								break;
							case CssGrammar.OP_PAREN_END:
								nesting--;
								break;
							case CssGrammar.OP_ATTR_END:
								if (nesting <= 0) {
									// trim token value, terminate accessor
									next = new CssToken(next.getToken(), value.substring(1), next.getIndex()+1, next.getLine(), next.getColumn()+1);
									// inject a joiner which will signal that what follows is attached to the accessor
									parent.appendChild(new CombinatorNode(CombinatorType.SELF, next.getIndex(), next.getLine(), next.getColumn()));
									return;
								}
								break;
						}
					}

					parseValue(args, next, isSelector, NODE_CONTEXT.ACCESSOR);
					continue;
			}
		}
	}

	private void parseValue(ContainerNode parent, CssToken token, boolean isSelector, NODE_CONTEXT context) {

		switch (token.getToken()) {
			case FUNCTION:
				FunctionNode func = parseFunction(parent, token, isSelector);

				// As of v21.0.1180.89, Chrome ignores saturation & brightness levels of "0" without percentage units.
				// This is a workaround which forces units to be output for these two arguments.
				String fn = func.getValue(true);
				if (HSL.equals(fn) || HSLA.equals(fn)) {
					// NOTE: args include the commas as operators
					List<CssNode> args = func.getContainer().getChildren();
					if (args.size() > 2) {
						// HSL/A saturation percentage
						CssNode arg = args.get(2);
						if (arg instanceof NumericNode) {
							((NumericNode)arg).setKeepUnits(true);
						}

						if (args.size() > 4) {
							// HSL/A brightness percentage
							arg = args.get(4);
							if (arg instanceof NumericNode) {
								((NumericNode)arg).setKeepUnits(true);
							}
						}
					}
				}
				break;

			case ACCESSOR:
				parseAccessor(parent, token, isSelector);
				break;

			case OPERATOR:
				parent.appendChild(new OperatorNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				break;

			case NUMERIC:
				if (isSelector) {
					// numeric is class selector
					parent.appendChild(new ValueNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				} else {
					parent.appendChild(new NumericNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				}
				break;

			case COLOR:
				if (isSelector) {
					// color is ID selector
					parent.appendChild(new ValueNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				} else {
					parent.appendChild(new ColorNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				}
				break;

			case STRING:
				parent.appendChild(new StringNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				break;

			case VALUE:
				parent.appendChild(new ValueNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				break;

			case COMMENT:
				parent.appendChild(new CommentNode(token.getValue(), token.getIndex(), token.getLine(), token.getColumn()));
				break;

			case ERROR:
				throw throwErrorToken(token);

			case RULE_DELIM:
				if (context == NODE_CONTEXT.FUNCTION) {
					// Data URIs contain semicolons
					parent.appendChild(new OperatorNode(Character.toString(CssGrammar.OP_DECL_DELIM), token.getIndex(), token.getLine(), token.getColumn()));
					break;
				}

			default:
				throw new InvalidTokenException("Invalid token in "+context+": "+token, token);
		}

		if (next == token) {
			// consume token
			next = null;
		}
	}

	private void evalExpressions(DeclarationNode declaration, boolean throwOnError) {
		try {
			ValueNode result = ArithmeticEvaluator.eval(declaration.getChildren());
			declaration.getChildren().clear();
			declaration.appendChild(result);

		} catch (InvalidNodeException ex) {
			// suppress errors when evaluation not required
			if (throwOnError) { throw ex; }
		}
	}

	private void evalMixins(RuleSetNode targetSet, RuleSetNode nestedSet) {
		if (targetSet == null) {
			throw new SyntaxException("Invalid sequence in rule-set", nestedSet.getIndex(), nestedSet.getLine(), nestedSet.getColumn());
		}

		// remove ruleSet from parent
		nestedSet.getParent().removeChild(nestedSet);

		for (SelectorNode selector : nestedSet.getSelectors()) {
			// look up mixin rules with selector
			for (CssNode node : targetSet.getParent().getChildren()) {
				RuleSetNode mixin = (node instanceof RuleSetNode) ? (RuleSetNode)node : null; 
				if (mixin == null || !mixin.getSelectors().contains(selector)) {
					continue;
				}

				// add mixin to parent
				for (CssNode child : mixin.getChildren()) {
					// TODO: clone node
					targetSet.appendChild(child);
				}
			}
		}
	}

	private void parseBlock(BlockNode block, boolean isRuleSet)
		throws IOException {

		// consume block begin
		next = null;

		while (hasNext() && !CssTokenType.BLOCK_END.equals(next.getToken())) {
			parseStatement(block, isRuleSet);
		}

		// consume block end
		next = null;
	}

	private InvalidTokenException throwErrorToken(CssToken token) {
		// TODO: back with interface?
		if (tokens instanceof CssLexer) {
			return new InvalidTokenException("Syntax error: "+token, token, ((CssLexer)tokens).getLastError());
		}

		return new InvalidTokenException("Syntax error: "+token, token);
	}

	/**
	 * Ensures the next node is ready
	 * @return
	 */
	private boolean hasNext() {
		// ensure non-null value
		while (next == null && tokens.hasNext()) {
			next = tokens.next();
		}

		return (next != null);
	}
}
