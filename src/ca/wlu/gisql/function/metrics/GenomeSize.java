package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Totals;

public class GenomeSize extends MetricFunction<Totals> {
	public static final Function self = new GenomeSize();

	private GenomeSize() {
		super("genecount", "Count the number of genes with membership > 0",
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
