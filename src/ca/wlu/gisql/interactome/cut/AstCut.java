package ca.wlu.gisql.interactome.cut;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

class AstCut implements AstNode {
	private final double cutoff;
	private final AstNode parameter;

	AstCut(AstNode node, double cutoff) {
		parameter = node;
		this.cutoff = cutoff;
	}

	public Interactome asInteractome() {
		return new Cut(parameter.asInteractome(), cutoff);
	}

	public AstNode fork(AstNode substitute) {
		return new AstCut(parameter.fork(substitute), cutoff);
	}

	public int getPrecedence() {
		return Cut.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" [");
		print.print(cutoff);
		print.print("]");
	}

}