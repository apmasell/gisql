package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.metrics.Fuzziness;

public class InteractomeFuzziness extends MetricFunction<Fuzziness> {
	public static final Function self = new InteractomeFuzziness();

	private InteractomeFuzziness() {
		super("interactionfuzz",
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
