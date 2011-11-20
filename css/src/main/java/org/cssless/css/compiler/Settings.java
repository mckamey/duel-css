package org.cssless.css.compiler;

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
		return this.prettyPrint;
	}

	public void setPrettyPrint(boolean value) {
		this.prettyPrint = value;
	}

	public boolean getVerbose() {
		return this.verbose;
	}

	public void setVerbose(boolean value) {
		this.verbose = value;
	}
	
	public File getSource() {
		return this.source;
	}

	public void setSource(String value) {
		if (value == null || value.isEmpty()) {
			this.source = null;
			return;
		}

		this.source = new File(value.replace('\\', '/'));
	}

	public File getTarget() {
		if (this.target == null) {
			return this.getSource();
		}

		return this.target;
	}

	public void setTarget(String value) {
		if (value == null || value.isEmpty()) {
			this.target = null;
			return;
		}

		this.target = new File(value.replace('\\', '/'));
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

	private void findFiles(List<File> files, Set<String> extensions, File searchPath)
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
