package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.metrics.Metrics;
import ca.wlu.gisql.interactome.metrics.MetricsInteractome;
import ca.wlu.gisql.runner.ExpressionRunner;

/** Convenience wrapper to convert {@link Metrics} into {@link Function}s. */
public abstract class MetricFunction<M extends Metrics> extends Function {

	private final Class<M> metricclass;

	protected MetricFunction(ExpressionRunner runner, String name,
			String description, Type returntype, Class<M> metric) {
		super(runner, name, description, Type.InteractomeType, returntype);
		this.metricclass = metric;
	}

	protected abstract Object failure();

	@Override
	public final Object run(Object... parameters) {
		try {
			M metric = metricclass.newInstance();
			MetricsInteractome interactome = new MetricsInteractome(
					(Interactome) parameters[0], metric);
			if (interactome.process()) {
				return value(metric);
			} else {
				runner.getEnvironment().assertWarning(
						"Failed to process interactome.");
				return failure();
			}
		} catch (InstantiationException e) {
			runner.getEnvironment().assertWarning(e.toString());
			return failure();
		} catch (IllegalAccessException e) {
			runner.getEnvironment().assertWarning(e.toString());
			return failure();
		}
	}

	protected abstract Object value(M metric);
}
