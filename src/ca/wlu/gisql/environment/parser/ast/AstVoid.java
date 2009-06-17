package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public abstract class AstVoid implements AstNode {

	public Interactome asInteractome() {
		return null;
	}

	public abstract void execute();

	public AstNode fork(AstNode substitute) {
		return null;
	}

	public int getPrecedence() {
		return 0;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter print) {
		print.print(':');
		print.print(this.getClass().getName());
	}

	public String toString() {
		return ShowableStringBuilder.toString(this);
	}
}
