package org.duelengine.css.codegen;

import org.duelengine.css.ast.CssNode;

/**
 * Simple visitor which allows transforming the CSS AST before formatting
 */
public interface CssFilter {

	CssNode filter(CssNode node);
}
