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
				// TODO: LESS will use this for scoped variables
				if (isRuleSet) {
					throw new InvalidTokenException("Invalid token inside rule-set: "+this.next, this.next);
				}

				this.parseAtRule(parent);
				break;

			case VALUE:
			case STRING:
			case NUMERIC:
			case COLOR:
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
				throw throwErrorToken();

			case RULE_DELIM:
				// consume extraneous ';'
				this.next = null;
				break;

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

				case VALUE:
					atRule.appendChild(new ValueNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case STRING:
					atRule.appendChild(new StringNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case NUMERIC:
					atRule.appendChild(new NumericNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case COLOR:
					atRule.appendChild(new ColorNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case OPERATOR:
					atRule.appendChild(new OperatorNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case COMMENT:
					atRule.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case ERROR:
					throw throwErrorToken();

				default:
					throw new InvalidTokenException("Invalid token in at-rule: "+this.next, this.next);
			}
		}
	}

	private void parseRuleSet(ContainerNode parent, boolean nested)
		throws IOException {

		// consume ident
		CssToken ident = this.next;
		this.next = null;

		// if not nested, then must be rule set
		if (nested && this.hasNext() && CssTokenType.OPERATOR.equals(this.next.getToken()) && ":".equals(this.next.getValue())) {
			this.parseDeclaration(parent, ident);
			return;
		}

		RuleSetNode ruleSet = new RuleSetNode(ident.getIndex(), ident.getLine(), ident.getColumn());
		parent.appendChild(ruleSet);

		RuleSetNode nestedParent = (parent instanceof RuleSetNode) ? (RuleSetNode)parent : null;

		this.parseSelector(ruleSet, ident);

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					if (nestedParent != null) {
						// LESS allows nested rules, unroll selectors here
						ruleSet.expandSelectors(nestedParent.getSelectors());
					}
					this.parseBlock(ruleSet, true);
					return;

				case VALUE:
				case STRING:
				case NUMERIC:
				case COLOR:
				case OPERATOR:
					this.parseSelector(ruleSet, this.next);
					continue;

				case COMMENT:
					ruleSet.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case ERROR:
					throw throwErrorToken();

				default:
					throw new InvalidTokenException("Invalid token in rule-set: "+this.next, this.next);
			}
		}
	}

	private void parseSelector(RuleSetNode ruleSet, CssToken start)
		throws IOException {

		SelectorNode selector = new SelectorNode(start.getIndex(), start.getLine(), start.getColumn());
		ruleSet.getSelectors().add(selector);

		char ch;
		String value;
		int funcDepth = 0;

		// check identity of start
		if (start != this.next) {
			// need to process start token outside of loop due to look ahead needed for LESS nested rule-sets
			switch (start.getToken()) {
				case COMMENT:
					ruleSet.appendChild(new CommentNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn()));
					break;
				case OPERATOR:
					selector.appendChild(new OperatorNode(start.getValue(), start.getIndex(), start.getLine(), start.getColumn()));
					break;
				default:
					value = start.getValue();
					ch = (value != null) ? value.charAt(value.length()-1) : '\0';
					if (ch == CssGrammar.OP_PAREN_BEGIN || ch == CssGrammar.OP_ATTR_BEGIN) {
						funcDepth++;
					}
					selector.appendChild(new ValueNode(value, start.getIndex(), start.getLine(), start.getColumn()));
					break;
			}
		}

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					// terminate selector
					return;

				case VALUE:
				case NUMERIC:
				case COLOR:
					// numeric/color are typically ID and class selectors
					value = this.next.getValue();
					ch = (value != null) ? value.charAt(value.length()-1) : '\0';
					if (ch == CssGrammar.OP_PAREN_BEGIN || ch == CssGrammar.OP_ATTR_BEGIN) {
						funcDepth++;
					}
					selector.appendChild(new ValueNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case OPERATOR:
					value = this.next.getValue();
					if (funcDepth <= 0) {
						if (",".equals(value)) {
							// consume token
							this.next = null;
							// terminate selector
							return;
						}

						CombinatorType combinator = CombinatorNode.getCombinator(value);
						if (combinator != null) {
							selector.appendChild(new CombinatorNode(combinator, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
							// consume token
							this.next = null;
							continue;
						}
					}

					ch = (value != null) ? value.charAt(0) : '\0';
					if (ch == CssGrammar.OP_PAREN_END || ch == CssGrammar.OP_ATTR_END) {
						funcDepth--;
					}
					selector.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));

					// consume token
					this.next = null;
					continue;

				case STRING:
					selector.appendChild(new StringNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case COMMENT:
					ruleSet.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case ERROR:
					throw throwErrorToken();

				default:
					throw new InvalidTokenException("Invalid token in selector: "+this.next, this.next);
			}
		}
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

		char ch;
		String value;
		int funcDepth = 0;
		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_END:
					if (requiredEval || optionalEval) {
						this.evalExpressions(declaration, requiredEval);
					}
					return;

				case AT_RULE:
					// LESS variable references leverage @rule syntax
					value = this.next.getValue();
					declaration.appendChild(new LessVariableReferenceNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					requiredEval = true;
					// consume token
					this.next = null;
					continue;

				case RULE_DELIM:
					if (funcDepth <= 0) {
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

				case VALUE:
					value = this.next.getValue();
					ch = (value != null) ? value.charAt(value.length()-1) : '\0';
					if (ch == CssGrammar.OP_PAREN_BEGIN || ch == CssGrammar.OP_ATTR_BEGIN) {
						funcDepth++;
					}
					declaration.appendChild(new ValueNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case STRING:
					declaration.appendChild(new StringNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case NUMERIC:
					declaration.appendChild(new NumericNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case COLOR:
					declaration.appendChild(new ColorNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case OPERATOR:
					value = this.next.getValue();
					ch = (value != null && value.length() == 1) ? value.charAt(0) : '\0';
					if (ch == CssGrammar.OP_PAREN_END || ch == CssGrammar.OP_ATTR_END) {
						funcDepth--;
					}
					declaration.appendChild(new OperatorNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					switch (ch) {
						case '+':
						case '-':
						case '*':
						case '/':
							optionalEval = true;
							break;
					}
					// consume token
					this.next = null;
					continue;

				case IMPORTANT:
					declaration.setImportant(true);
					// consume token
					this.next = null;
					continue;

				case COMMENT:
					declaration.appendChild(new CommentNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;

				case ERROR:
					throw throwErrorToken();

				default:
					throw new InvalidTokenException("Invalid token in declaration: "+this.next, this.next);
			}
		}

		if (requiredEval || optionalEval) {
			this.evalExpressions(declaration, requiredEval);
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

	private InvalidTokenException throwErrorToken() {
		// TODO: back with interface?
		if (this.tokens instanceof CssLexer) {
			return new InvalidTokenException("Syntax error: "+this.next, this.next, ((CssLexer)this.tokens).getLastError());
		}

		return new InvalidTokenException("Syntax error: "+this.next, this.next);
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
