package org.duelengine.css.ast;

import org.duelengine.css.parsing.SyntaxException;

/**
 * Represents a selector combinator
 */
public class CombinatorNode extends ValueNode {

	private CombinatorType combinator;

	public CombinatorNode(CombinatorType combinator, int index, int line, int column) {
		super(mapCombinator(combinator, index, line, column), index, line, column);

		this.combinator = combinator;
	}

	public CombinatorNode(CombinatorType combinator) {
		super(mapCombinator(combinator, -1, -1, -1));

		this.combinator = combinator;
	}

	@Override
	public CssNodeType getNodeType() {
		return CssNodeType.COMBINATOR;
	}

	public void setCombinator(CombinatorType combinator) {
		this.combinator = combinator;
		super.setValue(mapCombinator(combinator, -1, -1, -1));
	}

	@Override
	public void setValue(String value) {
		this.combinator = getCombinator(value);
		super.setValue(value);
	}

	protected static String mapCombinator(CombinatorType combinator, int index, int line, int column) {
		if (combinator == null) {
			throw new NullPointerException("combinator");
		}

		switch (combinator) {
			case ADJACENT:
				return "+";
			case CHILD:
				return ">";
			case DESCENDANT:
				return " ";
			case SIBLING:
				return "~";
			case SELF:
				return "";
			default:
				throw new SyntaxException("Invalid combinator type: "+combinator, index, line, column);
		}
	}

	public static CombinatorType getCombinator(String combinator) {
		if (combinator == null || combinator.length() != 1) {
			return null;
		}

		switch (combinator.charAt(0)) {
			case '+':
				return CombinatorType.ADJACENT;
			case '>':
				return CombinatorType.CHILD;
			case ' ':
				return CombinatorType.DESCENDANT;
			case '~':
				return CombinatorType.SIBLING;
			default:
				return null;
		}
	}

	@Override
	public WordBreak getWordBreak(boolean prettyPrint) {
		if (this.combinator == CombinatorType.SELF) {
			return WordBreak.NONE;
		}

		return prettyPrint ? WordBreak.BOTH : WordBreak.NONE;
	}

	@Override
	public boolean equals(Object arg) {
		if (!(arg instanceof CombinatorNode)) {
			// includes null
			return false;
		}

		CombinatorNode that = (CombinatorNode)arg;
		if (this.combinator == null ? that.combinator != null : !this.combinator.equals(that.combinator)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int HASH_PRIME = 1000003;

		int hash = 0;
		if (this.combinator != null) {
			hash = hash * HASH_PRIME + this.combinator.hashCode();
		}
		return hash;
	}
}
