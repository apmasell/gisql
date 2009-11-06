package ca.wlu.gisql.runner;

import java.io.File;

/** A context representing an entire file. */
public class FileContext extends ExpressionContext {

	private final File file;

	public FileContext(File file) {
		this.file = file;
	}

	/** Get a subcontext for a specific line in the file. */
	public LineContext getContextForLine(int linenumber, String line) {
		return new FileLineContext(this, linenumber, line);
	}

	/** Get the underlying file. */
	public File getFile() {
		return file;
	}

}
