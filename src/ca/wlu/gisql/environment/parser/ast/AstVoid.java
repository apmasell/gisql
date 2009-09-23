package ca.wlu.gisql.environment.parser.ast;

import ca.wlu.gisql.environment.parser.Parser;
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
		return Parser.PREC_FUNCTION;
	}

	public boolean isInteractome() {
		return false;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(':');
		print.print(this.getClass().getSimpleName());
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}
