package org.cssless.css.parsing;

import java.util.*;
import java.io.IOException;
import org.cssless.css.ast.*;

/**
 * Processes a token sequence into AST
 */
public class CssParser {

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
				} else {
					this.parseAtRule(parent);
				}
				break;

			case VALUE:
			case STRING:
			case NUMERIC:
			case COLOR:
			case OPERATOR:
				if (isRuleSet) {
					// TODO: LESS can have nested rule-sets
					this.parseDeclaration(parent);
				} else {
					this.parseRuleSet(parent);
				}
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
			this.parseDeclaration(parent);
			return;
		}

		AtRuleNode atRule = new AtRuleNode(keyword, this.next.getIndex(), this.next.getLine(), this.next.getColumn());
		parent.appendChild(atRule);
		// consume at-rule
		this.next = null;

		boolean done = false;
		while (!done && this.hasNext()) {
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

	private void parseRuleSet(ContainerNode parent)
		throws IOException {

		RuleSetNode ruleSet = new RuleSetNode(this.next.getIndex(), this.next.getLine(), this.next.getColumn());
		parent.appendChild(ruleSet);

		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					this.parseBlock(ruleSet, true);
					return;

				case VALUE:
				case STRING:
				case NUMERIC:
				case COLOR:
				case OPERATOR:
					this.parseSelector(ruleSet);
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

	private void parseSelector(RuleSetNode ruleSet)
		throws IOException {

		SelectorNode selector = new SelectorNode(this.next.getIndex(), this.next.getLine(), this.next.getColumn());
		ruleSet.getSelectors().add(selector);

		char ch;
		String value;
		int funcDepth = 0;
		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_BEGIN:
					// terminate selector
					return;

				case VALUE:
					value = this.next.getValue();
					ch = (value != null) ? value.charAt(value.length()-1) : '\0';
					if (ch == CssGrammar.OP_PAREN_BEGIN || ch == CssGrammar.OP_ATTR_BEGIN) {
						funcDepth++;
					}
					selector.appendChild(new ValueNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
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

				case NUMERIC:
				case COLOR:
					// these are typically ID and class selectors
					selector.appendChild(new ValueNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
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

	private void parseDeclaration(ContainerNode parent) {

		// LESS variable declarations leverage @rule syntax
		DeclarationNode declaration = (this.next.getToken() == CssTokenType.AT_RULE) ?
			new LessVariableDeclarationNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn()) :
			new DeclarationNode(this.next.getValue(), this.next.getIndex(), this.next.getLine(), this.next.getColumn());

		parent.appendChild(declaration);
		// consume property
		this.next = null;

		if (!this.hasNext() || !CssTokenType.OPERATOR.equals(this.next.getToken()) || !":".equals(this.next.getValue())) {
			throw new InvalidTokenException("Invalid declaration: "+this.next, this.next);
		}

		// consume ':'
		this.next = null;

		char ch;
		String value;
		int funcDepth = 0;
		while (this.hasNext()) {
			switch (this.next.getToken()) {
				case BLOCK_END:
					return;

				case AT_RULE:
					// LESS variable references leverage @rule syntax
					value = this.next.getValue();
					declaration.appendChild(new LessVariableReferenceNode(value, this.next.getIndex(), this.next.getLine(), this.next.getColumn()));
					// consume token
					this.next = null;
					continue;
					
				case RULE_DELIM:
					if (funcDepth <= 0) {
						// consume ';' as end of declaration
						this.next = null;
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
					ch = (value != null) ? value.charAt(0) : '\0';
					if (ch == CssGrammar.OP_PAREN_END || ch == CssGrammar.OP_ATTR_END) {
						funcDepth--;
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
