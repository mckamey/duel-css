package org.cssless.css.parsing;

import java.util.*;
import java.io.IOException;
import org.cssless.css.ast.*;
import org.cssless.css.codegen.ArithmeticEvaluator;

/**
 * Processes a token sequence into AST
 */
public class CssParser {

	private static final ArithmeticEvaluator evaluator = new ArithmeticEvaluator();

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

		return this.parse(tokens != null ? Arrays.asList(tokens).iterator() : null);
	}

	/**
	 * Parses token sequence into AST
	 * @param tokens
	 * @return
	 * @throws IOException 
	 */
	public StyleSheetNode parse(Iterable<CssToken> tokens)
		throws IOException {

		return this.parse(tokens != null ? tokens.iterator() : null);
	}

	/**
	 * Parses token sequence into AST
	 * @param tokens
	 * @return
	 */
	public StyleSheetNode parse(Iterator<CssToken> tokens)
		throws IOException {

		if (tokens == null) {
			throw new NullPointerException("tokens");
		}

		this.tokens = tokens;
		try {
			StyleSheetNode document = new StyleSheetNode(0, 0, 0);
			while (this.hasNext()) {
				this.parseStatement(document, false);
			}
			return document;

		} finally {
			this.tokens = null;
			this.next = null;
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

		switch (this.next.getToken()) {
			case AT_RULE:
				if (isRuleSet) {
					throw new InvalidTokenException("Invalid token inside rule-set: "+this.next, this.next);
				}

				// LESS also uses this for scoped variables
				this.parseAtRule(parent);
				break;

			case RULE_DELIM:
				// consume extraneous ';'
				this.next = null;
				break;

			case FUNCTION:
			case ACCESSOR:
			case STRING:
			case NUMERIC:
			case COLOR:
			case VALUE:
			case OPERATOR:
				// LESS can have nested rule-sets
				this.parseRuleSet(parent, isRuleSet);
				break;

			case COMMENT:
				parent.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
				// consume token
				this.next = null;
				break;

			case ERROR:
				throw this.throwErrorToken(this.next);

			default:
				throw new InvalidTokenException("Invalid token: "+this.next, this.next);
		}
	}

	private void parseAtRule(ContainerNode parent)
		throws IOException {

		String keyword = this.next.getValue();
		if (!CssGrammar.isAtRuleKeyword(keyword)) {
			CssToken ident = this.next;
			this.next = null;
			this.parseDeclaration(parent, ident);
			return;
		}

		AtRuleNode atRule = new AtRuleNode(keyword, this.next.getIndex(), this.next.getLine(), this.next.getColumn());
		parent.appendChild(atRule);
		// consume at-rule
		this.next = null;

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					BlockNode block = new BlockNode(this.next.getIndex(), this.next.getLine(), this.next.getColumn());
					atRule.setBlock(block);
					boolean asRuleSet = !"media".equals(atRule.getKeyword());
					this.parseBlock(block, asRuleSet);
					return;

				case RULE_DELIM:
					// consume token
					this.next = null;
					return;

				default:
					this.parseValue(atRule, this.next, false, "at-rule");
					continue;
			}
		}
	}

	private void parseRuleSet(ContainerNode parent, boolean nested)
		throws IOException {

		// consume ident
		CssToken ident = this.next;
		this.next = null;

		// if nested, then must be rule set
		if (nested && this.hasNext() && CssTokenType.OPERATOR.equals(this.next.getToken()) && ":".equals(this.next.getValue())) {
			this.parseDeclaration(parent, ident);
			return;
		}

		RuleSetNode ruleSet = new RuleSetNode(ident.getIndex(), ident.getLine(), ident.getColumn());
		parent.appendChild(ruleSet);

		RuleSetNode nestedParent = (parent instanceof RuleSetNode) ? (RuleSetNode)parent : null;

		if (this.parseSelector(ruleSet, ident)) {
			if (!nested) {
				throw new InvalidTokenException("Invalid sequence in rule-set: "+ident, ident);
			}

			// not a selector but a mixin
			this.evalMixins(nestedParent, ruleSet);
			return;
		}

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					if (nestedParent != null) {
						// LESS allows nested rules, unroll selectors here
						ruleSet.expandSelectors(nestedParent.getSelectors());
					}
					this.parseBlock(ruleSet, true);
					return;

				case ACCESSOR:
				case FUNCTION:
				case NUMERIC:
				case COLOR:
				case STRING:
				case VALUE:
				case OPERATOR:
					if (this.parseSelector(ruleSet, this.next)){
						if (!nested) {
							throw new InvalidTokenException("Invalid sequence in rule-set: "+ident, ident);
						}

						// not a selector but a mixin
						this.evalMixins(nestedParent, ruleSet);
						return;
					}
					continue;

				case COMMENT:
					ruleSet.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case ERROR:
					throw this.throwErrorToken(this.next);

				case BLOCK_END:
					if (!nested) {
						throw new InvalidTokenException("Invalid token in rule-set: "+this.next, this.next);
					}

					// allow parent to consume token
					return;

				default:
					throw new InvalidTokenException("Invalid token in rule-set: "+this.next, this.next);
			}
		}
	}

	private boolean parseSelector(RuleSetNode ruleSet, CssToken start)
		throws IOException {

		SelectorNode selector = new SelectorNode(start.getIndex(), start.getLine(), start.getColumn());
		ruleSet.addSelector(selector);

		int nesting = 0;

		// check identity of start
		if (start != this.next) {
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
					this.parseValue(selector, start, true, "selector");
					break;
			}
		}

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					// terminate selector
					return false;

				case BLOCK_END:
					// signal was mixin
					return true;

				case RULE_DELIM:
					// consume delim
					this.next = null;
					// signal was mixin
					return true;
					
				case OPERATOR:
					String value = this.next.getValue();
					if (value != null) {
						if (nesting <= 0) {
							if (",".equals(value)) {
								// consume delim
								this.next = null;
								// terminate selector
								return false;
							}

							CombinatorType combinator = CombinatorNode.getCombinator(value);
							if (combinator != null) {
								selector.appendChild(new CombinatorNode(combinator, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
								// consume token
								this.next = null;
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

					selector.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				default:
					this.parseValue(selector, this.next, true, "selector");
					continue;
			}
		}

		return false;
	}

	private void parseDeclaration(ContainerNode parent, CssToken ident) {

		if (!this.hasNext() || !CssTokenType.OPERATOR.equals(this.next.getToken()) || !":".equals(this.next.getValue())) {
			throw new InvalidTokenException("Invalid declaration: "+ident, ident);
		}
		// consume ':'
		this.next = null;

		boolean requiredEval = (ident.getToken() == CssTokenType.AT_RULE);
		boolean optionalEval = false;

		// LESS variable declarations leverage @rule syntax
		DeclarationNode declaration = requiredEval ?
			new LessVariableDeclarationNode(ident.getValue(), ident.getIndex(), ident.getLine(), ident.getColumn()) :
			new DeclarationNode(ident.getValue(), ident.getIndex(), ident.getLine(), ident.getColumn());

		parent.appendChild(declaration);

		int nesting = 0;

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_END:
					if (requiredEval || optionalEval) {
						this.evalExpressions(declaration, requiredEval);
					}
					return;

				case AT_RULE:
					// LESS variable references leverage @rule syntax
					declaration.appendChild(new LessVariableReferenceNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					requiredEval = true;
					// consume token
					this.next = null;
					continue;

				case RULE_DELIM:
					if (nesting <= 0) {
						// consume ';' as end of declaration
						this.next = null;
						if (requiredEval || optionalEval) {
							this.evalExpressions(declaration, requiredEval);
						}
						return;
					}
					// still within function
					declaration.appendChild(new OperatorNode(";", this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case OPERATOR:
					String value = this.next.getValue();
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
										this.evalExpressions(declaration, requiredEval);
									}
									return;
								}
								break;
//							case ',':
//								if (nesting <= 0) {
//									// consume token
//									this.next = null;
//									// end of LESS mixin function param
//									if (requiredEval || optionalEval) {
//										this.evalExpressions(declaration, requiredEval);
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

					declaration.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case IMPORTANT:
					declaration.setImportant(true);
					// consume token
					this.next = null;
					continue;

				default:
					this.parseValue(declaration, this.next, false, "declaration");
					continue;
			}
		}

		if (requiredEval || optionalEval) {
			this.evalExpressions(declaration, requiredEval);
		}
	}

	private void parseFunction(ContainerNode parent, CssToken start, boolean isSelector) {

		FunctionNode func = new FunctionNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn());
		parent.appendChild(func);
		if (this.next == start) {
			// consume function start
			this.next = null;
		}

		int nesting = 0;
		ContainerNode args = func.getContainer();

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case OPERATOR:
					String value = this.next.getValue();
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
									this.next = null;
									return;
								}
								nesting--;
								break;
							case ',':
								// TODO: check if children are LESS vars and consume?
								break;
						}
					}

					args.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case AT_RULE:
					// LESS mixin function param
					CssToken ident = this.next;
					this.next = null;
					this.parseDeclaration(args, ident);
					continue;

				default:
					this.parseValue(args, this.next, isSelector, "function");
					continue;
			}
		}
	}

	private void parseAccessor(ContainerNode parent, CssToken start, boolean isSelector) {

		AccessorNode accessor = new AccessorNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn());
		parent.appendChild(accessor);
		if (this.next == start) {
			// consume function start
			this.next = null;
		}

		int nesting = 0;
		ContainerNode args = accessor.getContainer();

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case OPERATOR:
					String value = this.next.getValue();
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
									this.next = null;
									return;
								}
								break;
						}
					}

					args.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				default:
					this.parseValue(args, this.next, isSelector, "accessor");
					continue;
			}
		}
	}

	private void parseValue(ContainerNode parent, CssToken token, boolean isSelector, String label) {

		switch (token.getToken()) {
			case FUNCTION:
				this.parseFunction(parent, token, isSelector);
				break;

			case ACCESSOR:
				this.parseAccessor(parent, token, isSelector);
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
				throw this.throwErrorToken(token);

			default:
				throw new InvalidTokenException("Invalid token in "+label+": "+token, token);
		}

		if (this.next == token) {
			// consume token
			this.next = null;
		}
	}

	private void evalExpressions(DeclarationNode declaration, boolean throwOnError) {
		try {
			ValueNode result = evaluator.eval(declaration.getChildren());
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
		this.next = null;

		while (this.hasNext() && !CssTokenType.BLOCK_END.equals(this.next.getToken())) {
			this.parseStatement(block, isRuleSet);
		}

		// consume block end
		this.next = null;
	}

	private InvalidTokenException throwErrorToken(CssToken token) {
		// TODO: back with interface?
		if (this.tokens instanceof CssLexer) {
			return new InvalidTokenException("Syntax error: "+token, token, ((CssLexer)this.tokens).getLastError());
		}

		return new InvalidTokenException("Syntax error: "+token, token);
	}

	/**
	 * Ensures the next node is ready
	 * @return
	 */
	private boolean hasNext() {
		// ensure non-null value
		while (this.next == null && this.tokens.hasNext()) {
			this.next = this.tokens.next();
		}

		return (this.next != null);
	}
}
