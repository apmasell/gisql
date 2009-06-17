package ca.wlu.gisql.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class ShowablePrintWriter extends PrintWriter {

	public ShowablePrintWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public ShowablePrintWriter(File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	public ShowablePrintWriter(OutputStream out) {
		super(out);
	}

	public ShowablePrintWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public ShowablePrintWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public ShowablePrintWriter(String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public ShowablePrintWriter(Writer out) {
		super(out);
	}

	public ShowablePrintWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public void print(Prioritizable prioritizable, int precedence) {
		if (prioritizable.getPrecedence() < precedence)
			print("(");
		prioritizable.show(this);
		if (prioritizable.getPrecedence() < precedence)
			print(")");
	}

	public void print(Show showable) {
		showable.show(this);
	}

	public void println(Prioritizable prioritizable, int precdence) {
		print(prioritizable, precdence);
		println();
	}

	public void println(Show showable) {
		print(showable);
		println();
	}
}
