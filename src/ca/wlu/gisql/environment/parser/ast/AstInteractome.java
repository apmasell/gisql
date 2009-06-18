package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstInteractome implements AstNode {

	private final Interactome interactome;

	public AstInteractome(Interactome interactome) {
		super();
		this.interactome = interactome;
	}

	public Interactome asInteractome() {
		return interactome;
	}

	public boolean equals(Object other) {
		if (other instanceof AstInteractome) {
			return interactome == ((AstInteractome) other).interactome;

		} else {
			return false;
		}
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public int getPrecedence() {
		return interactome.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter print) {
		print.print(interactome);
	}

	public String toString() {
		return interactome.toString();
	}

}
