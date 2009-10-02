package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Totals;

public class InteractomeSize extends MetricFunction<Totals> {
	public static final Function self = new InteractomeSize();

	private InteractomeSize() {
		super("interactioncount",
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
