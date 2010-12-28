package org.cssless.css.ast;

/**
 * Interface for nodes requiring LESS pre-processing
 */
public interface LessNode {

	/**
	 * Evaluates LESS node producing static content and modifying metadata
	 * May return null if does not generate any content
	 * @param context
	 * @return
	 */
	public CssNode eval(ContainerNode context);
}
