package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Fuzziness;
import ca.wlu.gisql.runner.ExpressionRunner;

public class GenomeFuzziness extends MetricFunction<Fuzziness> {

	public GenomeFuzziness(ExpressionRunner runner) {
		super(runner, "genefuzz",
				"Calculate the fuzziness of the gene memberships",
				Type.RealType, Fuzziness.class);
	}

	@Override
	protected Object failure() {
		return 0.0;
	}

	@Override
	protected Object value(Fuzziness metric) {
		return metric.getGeneFuziness();
	}

}
