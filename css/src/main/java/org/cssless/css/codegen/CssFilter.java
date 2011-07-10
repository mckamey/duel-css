package org.cssless.css.codegen;

import org.cssless.css.ast.CssNode;

/**
 * Simple visitor which allows transforming the CSS AST before formatting
 */
public interface CssFilter {

	CssNode filter(CssNode node);
}
