package org.duelengine.css.compiler;

public class CLI {

	private static final String SEPARATOR = "========================================";
	private static final String HELP = "java -jar css.jar\n"+
			"  --help                       : this help text\n"+
			"  -in <source-file|source-dir> : file path to the source file or folder (required)\n"+
			"  -out <target-dir>            : file path to the target output directory (default: <source-dir>)\n"+
			"  -pretty                      : pretty-prints the output\n"+
			"  -v                           : verbose output\n";

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(HELP);
			return;
		}

		Settings settings = new Settings();
		System.out.println(SEPARATOR);
		System.out.println("CSS/LESS for the JVM\n");
		for (int i=0; i<args.length; i++) {
			String arg = args[i];

			if ("-in".equals(arg)) {
				settings.setSource(args[++i]);

			} else if ("-out".equals(arg)) {
				settings.setTarget(args[++i]);

			} else if ("-pretty".equals(arg)) {
				settings.setPrettyPrint(true);

			} else if ("-v".equals(arg)) {
				settings.setVerbose(true);

			} else if ("--help".equalsIgnoreCase(arg)) {
				System.out.println(HELP);
				System.out.println(SEPARATOR);
				return;

			} else {
				System.out.println(HELP);
				System.out.println(SEPARATOR);
				return;
			}
		}

		try {
			new CssCompiler().execute(settings);

		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		System.out.println(SEPARATOR);
	}
}