package org.cssless.css.compiler;

import java.io.*;
import java.util.*;
import org.cssless.css.ast.*;
import org.cssless.css.codegen.*;
import org.cssless.css.parsing.*;

public class CssCompiler {

	private static final String HELP =
		"Usage:\n" +
		"\tjava -jar cssless.jar <input-file|input-folder> <output-folder>\n" +
		"\tinput-file: path to the CSS/LESS input file (e.g. foo.less)\n"+
		"\tinput-folder: path to the input folder containing CSS/LESS files\n"+
		"\toutput-folder: path to the output folder\n";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(HELP);
			return;
		}

		CssCompiler compiler = new CssCompiler();
		compiler.setInputRoot(args[0]);

		if (args.length > 1) {
			compiler.setOutputFolder(args[1]);
		}

		try {
			compiler.execute();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean verbose;
	private File inputRoot;
	private File outputFolder;

	public String getInputRoot() {
		return this.inputRoot.getAbsolutePath();
	}

	public void setInputRoot(String value) {
		this.inputRoot = (value != null) ? new File(value.replace('\\', '/')) : null;
	}

	public String getOutputFolder() {
		return this.outputFolder.getAbsolutePath();
	}

	public void setOutputFolder(String value) {
		this.outputFolder = (value != null) ? new File(value.replace('\\', '/')) : null;
	}

	private boolean ensureSettings() {
		if (this.inputRoot == null || !this.inputRoot.exists()) {
			System.err.println("Error: no input files found: "+this.inputRoot);
			return false;
		}

		if (this.outputFolder == null || !this.outputFolder.exists()) {
			System.err.println("Error: no output path found: "+this.outputFolder);
		}

		return true;
	}

	/**
	 * Compiles view files
	 * @throws IOException 
	 */
	public void execute() throws IOException {
		if (!this.ensureSettings()) {
			return;
		}

		List<File> inputFiles = findFiles(this.inputRoot);
		if (inputFiles.size() < 1) {
			System.err.println("this.inputRoot.getAbsolutePath(): Error: no input files found");
			return;
		}

		for (File inputFile : inputFiles) {
			StyleSheetNode stylesheet;
			try {
				FileReader reader = new FileReader(inputFile);
				stylesheet = new CssParser().parse(new CssLexer(reader));

			} catch (SyntaxException ex) {
				this.reportSyntaxError(inputFile, ex);
				continue;
			}

			if (stylesheet == null) {
				System.err.println(inputFile.getAbsolutePath()+": Syntax error: no view found");
				return;
			}

			CodeGenSettings settings = new CodeGenSettings();
			// TODO: allow setting of properties from args
//			settings.setIndent("\t");
//			settings.setNewline(System.getProperty("line.separator"));

			CssFormatter formatter = new CssFormatter(settings);
			try {
				File outputFile = new File(this.outputFolder, inputFile.getName()+formatter.getFileExtension());
				outputFile.getParentFile().mkdirs();

				FileWriter writer = new FileWriter(outputFile, false);
				try {
					formatter.write(writer, stylesheet);
				} finally {
					writer.flush();
					writer.close();
				}

			} catch (SyntaxException ex) {
				this.reportSyntaxError(inputFile, ex);
			}
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

			System.err.println(String.format(
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

			System.err.println(text);
			if (col > 0) {
				System.err.println(String.format("%"+col+"s", "^"));
			} else {
				System.err.println("^");
			}

			if (this.verbose) {
				ex.printStackTrace();
			}

		} catch (Exception ex2) {
			ex.printStackTrace();

			if (this.verbose) {
				ex2.printStackTrace();
			}
		}
	}

	private static List<File> findFiles(File inputRoot) {

		List<File> files = new ArrayList<File>();
		Queue<File> folders = new LinkedList<File>();
		folders.add(inputRoot);

		while (!folders.isEmpty()) {
			File file = folders.poll();
			if (file.isDirectory()) {
				folders.addAll(Arrays.asList(file.listFiles()));
			} else if (file.getName().toLowerCase().endsWith(".less")) {
				files.add(file);
			} else if (file.getName().toLowerCase().endsWith(".css")) {
				files.add(file);
			}
		}

		return files;
	}
}
