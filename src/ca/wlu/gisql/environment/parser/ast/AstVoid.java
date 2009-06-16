package ca.wlu.gisql.environment.parser.ast;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public abstract class AstVoid implements AstNode {

	public Interactome asInteractome() {
		return null;
	}

	public abstract void execute();

	public AstNode fork(AstNode substitute) {
		return null;
	}

	public boolean isInteractome() {
		return false;
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

	public String toString() {
		return show(new StringBuilder()).toString();
	}
}
