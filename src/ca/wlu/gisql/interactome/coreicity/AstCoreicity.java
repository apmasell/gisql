package ca.wlu.gisql.interactome.coreicity;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstCoreicity implements AstNode {

	private final int threshold;
	private final AstNode parameter;
	private final NumericComparison comparison;

	public AstCoreicity(AstNode parameter, NumericComparison comparison,
			int threshold) {
		this.parameter = parameter;
		this.comparison = comparison;
		this.threshold = threshold;
	}

	public Interactome asInteractome() {
		return new Coreicity(parameter.asInteractome(), comparison, threshold);
	}

	public AstNode fork(AstNode substitute) {
		return new AstCoreicity(parameter.fork(substitute), comparison,
				threshold);
	}

	public int getPrecedence() {
		return DeltaCoreicity.descriptor.getPrecedence();
	}

	public boolean isInteractome() {
		return true;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(parameter, getPrecedence());
		print.print(" :core ");
		print.print(comparison);
		print.print(' ');
		print.print(threshold);

	}

}
