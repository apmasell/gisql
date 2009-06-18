package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstInteger implements AstNode {
	private int value;

	public AstInteger(int value) {
		super();
		this.value = value;
	}

	public Interactome asInteractome() {
		return null;
	}

	public boolean equals(Object other) {
		if (other instanceof AstInteger) {
			return value == ((AstInteger) other).value;

		} else {
			return false;
		}
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public int getInt() {
		return value;
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter print) {
		print.print(value);
	}

	public String toString() {
		return Integer.toString(value);
	}

}
