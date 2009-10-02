package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Cardinality;

public class InteractomeCardinality extends MetricFunction<Cardinality> {
	public static final Function self = new InteractomeCardinality();

	private InteractomeCardinality() {
		super("genecard",
				"Calculate the fuzzy cardinality of the gene memberships",
				Type.RealType, Cardinality.class);
	}

	@Override
	protected Object failure() {
		return 0.0;
	}

	@Override
	protected Object value(Cardinality metric) {
		return metric.getGeneSize();
	}

}
