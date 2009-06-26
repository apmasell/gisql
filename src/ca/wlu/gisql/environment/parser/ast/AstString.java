package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.environment.parser.Parser;
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

	public boolean equals(Object other) {
		if (other instanceof AstString) {
			return value == ((AstString) other).value;

		} else {
			return false;
		}
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public String getString() {
		return value;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(value);
	}

	public String toString() {
		return value;
	}

}
