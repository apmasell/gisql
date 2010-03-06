package ca.wlu.gisql.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Provides an interface to write classes implementing {@link Show} to an
 * {@link OutputStream}. Printing may optionally have some kind of context.
 * Classes implementing {@link Prioritizable} will be written out with brackets.
 */
public class ShowablePrintWriter<E> extends PrintWriter {
	private final boolean closeable;
	private final E context;

	public ShowablePrintWriter(OutputStream out, E context) {
		super(out);
		this.context = context;
		closeable = true;
	}

	public ShowablePrintWriter(String filename, boolean append, E context)
			throws FileNotFoundException {
		super(filename == null ? System.out : new FileOutputStream(filename,
				append));
		this.context = context;
		closeable = filename != null;
	}

	public ShowablePrintWriter(Writer out, E context) {
		super(out);
		this.context = context;
		closeable = true;
	}

	@Override
	public void close() {
		if (closeable) {
			super.close();
		} else {
			flush();
		}
	}

	public E getContext() {
		return context;
	}

	@Override
	public final void print(Object value) {
		if (value instanceof String) {
			print('"');
			super.print(value);
			print('"');
		} else {
			super.print(value);
		}
	}

	public final <C extends Comparable<C>> void print(
			Prioritizable<E, C> prioritizable, C precedence) {
		boolean brackets = prioritizable.getPrecedence().compareTo(precedence) < 0;
		if (brackets) {
			print("(");
		}
		prioritizable.show(this);
		if (brackets) {
			print(")");
		}
	}

	public final void print(Show<E> showable) {
		showable.show(this);
	}

	public final <C extends Comparable<C>> void println(
			Prioritizable<E, C> prioritizable, C precdence) {
		print(prioritizable, precdence);
		println();
	}

	public final void println(Show<E> showable) {
		print(showable);
		println();
	}
}
