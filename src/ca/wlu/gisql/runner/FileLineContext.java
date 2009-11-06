package ca.wlu.gisql.runner;

/** The context of a single line inside of a file. */
public class FileLineContext extends LineContext {

	private final FileContext context;
	private final String line;
	private final int linenumber;

	public FileLineContext(FileContext context, int linenumber, String line) {
		this.context = context;
		this.linenumber = linenumber;
		this.line = line;
	}

	@Override
	public String getLine() {
		return line;
	}

	public int getLineNumber() {
		return linenumber;
	}

	public FileContext getParent() {
		return context;
	}

	@Override
	public String getSource() {
		return context.getFile().getName();
	}

}
