package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Cardinality;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenomeCardinality extends MetricFunction<Cardinality> {
	public GenomeCardinality(ExpressionRunner runner) {
		super(
				runner,
				"interactioncard",
				"Calculate the fuzzy cardinality of the interaction memberships",
				Type.RealType, Cardinality.class);
	}

	@Override
	protected Object failure() {
		return 0.0;
	}

	@Override
	protected Object value(Cardinality metric) {
		return metric.getInteractionSize();
	}

}
