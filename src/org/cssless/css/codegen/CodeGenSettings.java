package org.cssless.css.codegen;

/**
 * Settings which affect generated code
 */
public class CodeGenSettings {

	private boolean inlineBraces;
	private String indent;
	private String newline;

	public CodeGenSettings() {
		this(null, null, false);
	}

	public CodeGenSettings(String indent, String newline) {
		this(indent, newline, false);
	}

	public CodeGenSettings(String indent, String newline, boolean inlineBraces) {
		this.setIndent(indent);
		this.setNewline(newline);
		this.setInlineBraces(inlineBraces);
	}
	
	/**
	 * Gets the string used for source indentation
	 * @return
	 */
	public String getIndent() {
		return this.indent;
	}

	/**
	 * Sets the string used for source indentation
	 * @param value
	 */
	public void setIndent(String value) {
		this.indent = (value != null) ? value : "";
	}

	/**
	 * Gets the string used for line endings
	 * @return
	 */
	public String getNewline() {
		return this.newline;
	}

	/**
	 * Sets the string used for line endings
	 * @param value
	 */
	public void setNewline(String value) {
		this.newline = (value != null) ? value : "";
	}

	/**
	 * Gets if braces should be placed inline (rather than on own line) 
	 * @return
	 */
	public boolean useInlineBraces() {
		return this.inlineBraces;
	}

	/**
	 * Sets if braces should be placed inline (rather than on own line)
	 * @param value
	 */
	public void setInlineBraces(boolean value) {
		this.inlineBraces = value;
	}
}
