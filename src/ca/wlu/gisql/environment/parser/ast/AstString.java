package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstString implements AstNode {

	private String value;

	public AstString(String string) {
		super();
		this.value = string;
	}

	public Interactome asInteractome() {
		return null;
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public int getPrecedence() {
		return Integer.MAX_VALUE;
	}

	public String getString() {
		return value;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter print) {
		print.print(value);
	}

	public String toString() {
		return value;
	}

}
