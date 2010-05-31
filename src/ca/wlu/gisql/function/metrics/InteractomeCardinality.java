package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Cardinality;
import ca.wlu.gisql.runner.ExpressionRunner;

public class InteractomeCardinality extends MetricFunction<Cardinality> {

	public InteractomeCardinality(ExpressionRunner runner) {
		super(runner, "genecard",
				"Calculate the fuzzy cardinality of the gene memberships",
				Type.RealType, Cardinality.class);
	}

	@Override
	protected Object failure() {
		return -1.0;
	}

	@Override
	protected Object value(Cardinality metric) {
		return metric.getGeneSize();
	}

}
