package ca.wlu.gisql.runner;

public class SingleLineContext extends LineContext {

	private final String line;

	public SingleLineContext(String line) {
		this.line = line;
	}

	@Override
	public String getLine() {
		return line;
	}

	@Override
	public String getSource() {
		return "<stdin>";
	}

}
