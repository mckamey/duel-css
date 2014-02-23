package org.duelengine.css.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;

import org.duelengine.css.ast.StyleSheetNode;
import org.duelengine.css.codegen.CodeGenSettings;
import org.duelengine.css.codegen.CssFilter;
import org.duelengine.css.codegen.CssFormatter;
import org.duelengine.css.parsing.CssLexer;
import org.duelengine.css.parsing.CssParser;
import org.duelengine.css.parsing.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssCompiler {

	private static final Logger log = LoggerFactory.getLogger(CssCompiler.class);
	
	/**
	 * Processes CSS/LESS files
	 * @throws IOException 
	 */
	public void execute(Settings settings) throws IOException {
		if (settings == null) {
			throw new NullPointerException("settings");
		}

		List<File> inputFiles = settings.findFiles(".less", ".css");
		if (inputFiles.size() < 1) {
			log.error("Error: no input files found in "+settings.getSource());
			return;
		}

		CodeGenSettings formatSettings = new CodeGenSettings();
		if (settings.getPrettyPrint()) {
			formatSettings.setIndent("\t");
			formatSettings.setNewline(System.getProperty("line.separator"));
			formatSettings.setInlineBraces(true);
		}

		for (File inputFile : inputFiles) {
			String filename = inputFile.getName();
			int index = filename.lastIndexOf('.');
			if (index > 0) {
				filename = filename.substring(0, index);
			}
			this.process(inputFile, new File(settings.getTarget(), filename+CssFormatter.getFileExtension()), formatSettings, null, settings.getVerbose());
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
		this.process(source, target, settings, filter, false);
	}

	/**
	 * Processes a single CSS/LESS file
	 * @throws IOException 
	 */
	public void process(File source, File target, CodeGenSettings settings, CssFilter filter, boolean verbose) throws IOException {
		if (settings == null) {
			settings = new CodeGenSettings();
		}

		StyleSheetNode stylesheet;
		try {
			FileReader reader = new FileReader(source);
			stylesheet = new CssParser().parse(new CssLexer(reader));

		} catch (SyntaxException ex) {
			this.reportSyntaxError(source, ex, verbose);
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
			this.reportSyntaxError(source, ex, verbose);
		}
	}

	private void reportSyntaxError(File inputFile, SyntaxException ex, boolean verbose) {
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

			String text = "";
			LineNumberReader reader = null;
			try {
				reader = new LineNumberReader(new FileReader(inputFile));
				for (int i=-1; i<line; i++) {
					text = reader.readLine();
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			log.error(text);
			if (col > 0) {
				log.error(String.format("%"+col+"s", "^"));
			} else {
				log.error("^");
			}

			if (verbose) {
				ex.printStackTrace();
			}

		} catch (Exception ex2) {
			ex.printStackTrace();

			if (verbose) {
				ex2.printStackTrace();
			}
		}
	}
}
