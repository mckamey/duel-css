package org.duelengine.css.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Settings {

	private boolean prettyPrint;
	private boolean verbose;
	private File target;
	private File source;

	public boolean getPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean value) {
		prettyPrint = value;
	}

	public boolean getVerbose() {
		return verbose;
	}

	public void setVerbose(boolean value) {
		verbose = value;
	}
	
	public File getSource() {
		return source;
	}

	public void setSource(String value) {
		if (value == null || value.isEmpty()) {
			source = null;
			return;
		}

		source = new File(value.replace('\\', '/'));
	}

	public File getTarget() {
		if (target == null) {
			return getSource();
		}

		return target;
	}

	public void setTarget(String value) {
		if (value == null || value.isEmpty()) {
			target = null;
			return;
		}

		target = new File(value.replace('\\', '/'));
	}

	//-----------------

	File getTargetFile(String targetPath) {
		return new File(getTarget(), targetPath);
	}

	File findSourceFile(String sourcePath) {
		File source = new File(getSource()+sourcePath);
		if (source.exists()) {
			return source;
		}

		return null;
	}

	List<File> findFiles(String... extensions)
		throws IOException {

		List<File> files = new LinkedList<File>();

		Set<String> extSet = new HashSet<String>();
		for (String ext : extensions) {
			extSet.add(ext);
		}
		findFiles(files, extSet, getSource());

		return files;
	}

	private static void findFiles(List<File> files, Set<String> extensions, File searchPath)
			throws IOException {

		Queue<File> folders = new LinkedList<File>();

		folders.add(searchPath);
		while (!folders.isEmpty()) {
			File file = folders.remove();
			if (file == null) {
				continue;
			}

			if (file.isDirectory()) {
				folders.addAll(Arrays.asList(file.listFiles()));
				continue;
			}

			String ext = getExtension(file.getCanonicalPath());
			if (extensions.contains(ext)) {
				files.add(file);
			}
		}
	}

	private static String getExtension(String path) {
		int dot = path.lastIndexOf('.');
		if (dot < 0) {
			return "";
		}

		return path.substring(dot).toLowerCase();
	}
}
