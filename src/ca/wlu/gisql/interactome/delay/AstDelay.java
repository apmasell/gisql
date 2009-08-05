package ca.wlu.gisql.interactome.delay;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstDelay implements AstNode {
	private AstNode parameter;

	public AstDelay(AstNode parameter) {
		super();
		this.parameter = parameter;
	}

	public Interactome asInteractome() {
		return new Delay(parameter.asInteractome());
	}

	public AstNode fork(AstNode substitute) {
		return new AstDelay(parameter.fork(substitute));
	}

	public int getPrecedence() {
		return parameter.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter);
	}

}