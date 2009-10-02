package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Cardinality;

public class GenomeCardinality extends MetricFunction<Cardinality> {
	public static final Function self = new GenomeCardinality();

	private GenomeCardinality() {
		super(
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
