package ca.wlu.gisql.environment.parser.ast;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public class AstString implements AstNode {

	private String value;

	public AstString(String string) {
		super();
		this.value = string;
	}

	public Interactome asInteractome() {
		return null;
	}

	public AstNode fork(AstNode substitue) {
		return this;
	}

	public String getString() {
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
