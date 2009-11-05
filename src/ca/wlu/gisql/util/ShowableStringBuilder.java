package ca.wlu.gisql.util;

import java.io.StringWriter;

/**
 * Convenience version of {@link ShowablePrintWriter} that generates a string.
 * This is effectively {@link Show}'s equivalent of {@link StringBuilder}.
 */
public class ShowableStringBuilder<E> extends ShowablePrintWriter<E> {
	public static <E> String toString(Show<E> showable, E context) {
		ShowableStringBuilder<E> print = new ShowableStringBuilder<E>(context);
		print.print(showable);
		return print.toString();
	}

	private final StringWriter writer;

	public ShowableStringBuilder(E context) {
		super(new StringWriter(), context);
		writer = (StringWriter) super.out;
	}

	@Override
	public String toString() {
		return writer.toString();
	}
}
