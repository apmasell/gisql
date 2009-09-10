package ca.wlu.gisql.interactome.coreicity;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstDeltaCoreicity implements AstNode {

	private final int delta;
	private final AstNode parameter;
	private final NumericComparison comparison;

	public AstDeltaCoreicity(AstNode parameter, NumericComparison comparison,
			int delta) {
		this.parameter = parameter;
		this.comparison = comparison;
		this.delta = delta;
	}

	public Interactome asInteractome() {
		return new DeltaCoreicity(parameter.asInteractome(), comparison, delta);
	}

	public AstNode fork(AstNode substitute) {
		return new AstDeltaCoreicity(parameter.fork(substitute), comparison,
				delta);
	}

	public int getPrecedence() {
		return DeltaCoreicity.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" :deltacore ");
		print.print(comparison);
		print.print(' ');
		print.print(delta);

	}

}
