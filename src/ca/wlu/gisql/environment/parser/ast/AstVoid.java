package ca.wlu.gisql.environment.parser.ast;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public abstract class AstVoid implements AstNode {

	public AstNode fork(AstNode substitue) {
		return null;
	}

	public Interactome asInteractome() {
		return null;
	}

	public boolean isInteractome() {
		return false;
	}

	public abstract void execute();

	public String toString() {
		return show(new StringBuilder()).toString();
	}

	public PrintStream show(PrintStream print) {
		print.print(':');
		print.print(this.getClass().getName());
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(':');
		sb.append(this.getClass().getName());
		return sb;
	}
}
