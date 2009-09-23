package ca.wlu.gisql.interactome.coreicity;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstJaccardCoreicity implements AstNode {

	private final AstNode parameter;

	public AstJaccardCoreicity(AstNode parameter) {
		this.parameter = parameter;
	}

	public Interactome asInteractome() {
		return new JaccardCoreicity(parameter.asInteractome());
	}

	public AstNode fork(AstNode substitute) {
		return new AstJaccardCoreicity(parameter.fork(substitute));
	}

	public int getPrecedence() {
		return JaccardCoreicity.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" :jaccardcore");

	}

}
