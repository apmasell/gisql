package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstInteger implements AstNode {
	private long value;

	public AstInteger(long l) {
		super();
		this.value = l;
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

	public long getInt() {
		return value;
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(value);
	}

	public String toString() {
		return Long.toString(value);
	}

}
