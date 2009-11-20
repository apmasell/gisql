package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Totals;
import ca.wlu.gisql.runner.ExpressionRunner;

public class InteractomeSize extends MetricFunction<Totals> {

	public InteractomeSize(ExpressionRunner runner) {
		super(runner, "interactioncount",
				"Count the number of interactions with membership > 0",
				Type.NumberType, Totals.class);
	}

	@Override
	protected Object failure() {
		return 0;
	}

	@Override
	protected Object value(Totals metric) {
		return (long) metric.getInteractionCount();
	}

}
