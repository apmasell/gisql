package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstDouble implements AstNode {
	private double value;

	public AstDouble(double value) {
		super();
		this.value = value;
	}

	public Interactome asInteractome() {
		return null;
	}

	public AstNode fork(AstNode substitute) {
		return this;
	}

	public double getDouble() {
		return value;
	}

	public int getPrecedence() {
		return Integer.MAX_VALUE;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter print) {
		print.print(value);
	}

	public String toString() {
		return Double.toString(value);
	}
}
