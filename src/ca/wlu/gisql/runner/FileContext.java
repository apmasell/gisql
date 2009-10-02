package ca.wlu.gisql.runner;

import java.io.File;

public class FileContext extends ExpressionContext {

	private final File file;

	public FileContext(File file) {
		this.file = file;
	}

	public LineContext getContextForLine(int linenumber, String line) {
		return new FileLineContext(this, linenumber, line);
	}

	public File getFile() {
		return file;
	}

}
