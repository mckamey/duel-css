package org.cssless.css.compiler;

import java.io.*;
import java.util.*;

import org.cssless.css.ast.*;
import org.cssless.css.codegen.*;
import org.cssless.css.parsing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssCompiler {

	private static final Logger log = LoggerFactory.getLogger(CssCompiler.class);
	private final Settings settings;

	public CssCompiler(Settings settings) {
		if (settings == null) {
			throw new NullPointerException("settings");
		}
		this.settings = settings;
	}

	/**
	 * Processes CSS/LESS files
	 * @throws IOException 
	 */
	public void execute() throws IOException {
		List<File> inputFiles = this.settings.findFiles(".less", ".css");
		if (inputFiles.size() < 1) {
			log.error("Error: no input files found in "+this.settings.getSource());
			return;
		}

		CodeGenSettings settings = new CodeGenSettings();
		if (this.settings.getPrettyPrint()) {
			settings.setIndent("\t");
			settings.setNewline(System.getProperty("line.separator"));
			settings.setInlineBraces(true);
		}

		for (File inputFile : inputFiles) {
			String filename = inputFile.getName();
			int index = filename.lastIndexOf('.');
			if (index > 0) {
				filename = filename.substring(0, index);
			}
			this.process(inputFile, new File(this.settings.getTarget(), filename+CssFormatter.getFileExtension()), settings);
		}
	}

	/**
	 * Processes a single CSS/LESS file
	 * @throws IOException 
	 */
	public void process(File source, File target) throws IOException {
		this.process(source, target, null, null);
	}

	/**
	 * Processes a single CSS/LESS file
	 * @throws IOException 
	 */
	public void process(File source, File target, CodeGenSettings settings) throws IOException {
		this.process(source, target, settings, null);
	}

	/**
	 * Processes a single CSS/LESS file
	 * @throws IOException 
	 */
	public void process(File source, File target, CodeGenSettings settings, CssFilter filter) throws IOException {
		if (settings == null) {
			settings = new CodeGenSettings();
		}

		StyleSheetNode stylesheet;
		try {
			FileReader reader = new FileReader(source);
			stylesheet = new CssParser().parse(new CssLexer(reader));

		} catch (SyntaxException ex) {
			this.reportSyntaxError(source, ex);
			return;
		}

		if (stylesheet == null) {
			log.error("Syntax error: no stylesheet found in "+source.getAbsolutePath());
			return;
		}

		CssFormatter formatter = new CssFormatter(settings);
		try {
			target.getParentFile().mkdirs();

			FileWriter writer = new FileWriter(target, false);
			try {
				formatter.write(writer, stylesheet, filter);
			} finally {
				writer.flush();
				writer.close();
			}

		} catch (SyntaxException ex) {
			this.reportSyntaxError(source, ex);
		}
	}

	private void reportSyntaxError(File inputFile, SyntaxException ex) {
		try {
			String message = ex.getMessage();
			if (message == null) {
				if (ex.getCause() != null) {
					message = ex.getCause().getMessage();
				} else {
					message = "Error";
				}
			}

			log.error(String.format(
				"%s:%d: %s",
				inputFile.getAbsolutePath(),
				ex.getLine(),
				message));

			int col = ex.getColumn(),
				line=ex.getLine();

			LineNumberReader reader = new LineNumberReader(new FileReader(inputFile));
			String text = "";
			for (int i=-1; i<line; i++) {
				text = reader.readLine();
			}

			log.error(text);
			if (col > 0) {
				log.error(String.format("%"+col+"s", "^"));
			} else {
				log.error("^");
			}

			if (this.settings.getVerbose()) {
				ex.printStackTrace();
			}

		} catch (Exception ex2) {
			ex.printStackTrace();

			if (this.settings.getVerbose()) {
				ex2.printStackTrace();
			}
		}
	}
}
