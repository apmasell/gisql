package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Fuzziness;
import ca.wlu.gisql.runner.ExpressionRunner;

public class InteractomeFuzziness extends MetricFunction<Fuzziness> {

	public InteractomeFuzziness(ExpressionRunner runner) {
		super(runner, "interactionfuzz",
				"Calculate the fuzziness of the interaction memberships",
				Type.RealType, Fuzziness.class);
	}

	@Override
	protected Object failure() {
		return 0.0;
	}

	@Override
	protected Object value(Fuzziness metric) {
		return metric.getInteractionFuzziness();
	}

}
