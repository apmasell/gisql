package ca.wlu.gisql.environment.parser.ast;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public class AstInteger implements AstNode {
	private int value;

	public AstInteger(int value) {
		super();
		this.value = value;
	}

	public Interactome asInteractome() {
		return null;
	}

	public AstNode fork(AstNode substitue) {
		return this;
	}

	public int getInt() {
		return value;
	}

	public boolean isInteractome() {
		return false;
	}

	public PrintStream show(PrintStream print) {
		print.print(value);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(value);
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}

}
