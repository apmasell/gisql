package ca.wlu.gisql.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class ShowablePrintWriter<E> extends PrintWriter {
	private final E context;

	public ShowablePrintWriter(OutputStream out, E context) {
		super(out);
		this.context = context;
	}

	public ShowablePrintWriter(Writer out, E context) {
		super(out);
		this.context = context;
	}

	public E getContext() {
		return context;
	}

	public final void print(Prioritizable<E> prioritizable, int precedence) {
		if (prioritizable.getPrecedence() < precedence)
			print("(");
		prioritizable.show(this);
		if (prioritizable.getPrecedence() < precedence)
			print(")");
	}

	public final void print(Show<E> showable) {
		showable.show(this);
	}

	public final void println(Prioritizable<E> prioritizable, int precdence) {
		print(prioritizable, precdence);
		println();
	}

	public final void println(Show<E> showable) {
		print(showable);
		println();
	}
}
