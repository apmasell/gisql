package ca.wlu.gisql.function.metrics;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.metrics.Metrics;
import ca.wlu.gisql.interactome.metrics.MetricsInteractome;
import ca.wlu.gisql.vm.Machine;

public abstract class MetricFunction<M extends Metrics> extends Function {

	private final Class<M> metricclass;

	protected MetricFunction(String name, String description, Type returntype,
			Class<M> metric) {
		super(name, description, Type.InteractomeType, returntype);
		this.metricclass = metric;
	}

	protected abstract Object failure();

	@Override
	public final Object run(Machine machine, Object... parameters) {
		try {
			M metric = metricclass.newInstance();
			MetricsInteractome interactome = new MetricsInteractome(
					(Interactome) parameters[0], metric);
			if (interactome.process()) {
				return failure();
			} else {
				return value(metric);
			}
		} catch (InstantiationException e) {
			machine.getEnvironment().assertWarning(e.toString());
			return failure();
		} catch (IllegalAccessException e) {
			machine.getEnvironment().assertWarning(e.toString());
			return failure();
		}
	}

	protected abstract Object value(M metric);
}
