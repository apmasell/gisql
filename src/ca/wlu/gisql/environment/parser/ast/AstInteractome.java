package ca.wlu.gisql.environment.parser.ast;

import java.io.PrintStream;

import ca.wlu.gisql.interactome.Interactome;

public class AstInteractome implements AstNode {

	private Interactome interactome;

	public AstInteractome(Interactome interactome) {
		super();
		this.interactome = interactome;
	}

	public Interactome asInteractome() {
		return interactome;
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public boolean isInteractome() {
		return true;
	}

	public PrintStream show(PrintStream print) {
		interactome.show(print);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		interactome.show(sb);
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}

}
