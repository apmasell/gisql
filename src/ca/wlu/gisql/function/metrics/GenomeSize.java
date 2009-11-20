package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Totals;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenomeSize extends MetricFunction<Totals> {

	public GenomeSize(ExpressionRunner runner) {
		super(runner, "genecount",
				"Count the number of genes with membership > 0",
				Type.NumberType, Totals.class);
	}

	@Override
	protected Object failure() {
		return 0;
	}

	@Override
	protected Object value(Totals metric) {
		return (long) metric.getGeneCount();
	}

}
