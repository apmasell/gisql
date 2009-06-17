package ca.wlu.gisql.util;

import java.io.StringWriter;

public class ShowableStringBuilder extends ShowablePrintWriter {
	public static String toString(Show showable) {
		ShowableStringBuilder print = new ShowableStringBuilder();
		print.print(showable);
		return print.toString();
	}

	private final StringWriter writer;

	public ShowableStringBuilder() {
		super(new StringWriter());
		writer = (StringWriter) super.out;
	}

	public String toString() {
		return writer.toString();
	}
}
