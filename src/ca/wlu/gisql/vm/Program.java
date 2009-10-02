package ca.wlu.gisql.vm;

import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.util.Show;

public abstract class Program implements Show<Object> {

	public Program() {
		super();
	}

	public abstract Instruction get(int index);

	public final Object run(ExpressionRunListener listener,
			UserEnvironment environment, Object... parameters) {
		return this.run(new Machine(listener, environment), parameters);
	}

	public final Object run(Machine machine, Object... parameters) {
		return machine.enter(this, parameters);
	}
}
