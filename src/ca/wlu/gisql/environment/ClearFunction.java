package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;

public final class ClearFunction extends Function {
	class Clear extends Unit {
		private final Environment environment;

		private Clear(Environment environment) {
			this.environment = environment;
		}

		public boolean prepare() {
			EnvironmentUtils.clear(environment);
			return super.prepare();
		}
	}

	public static final Parseable descriptor = new ClearFunction();

	private ClearFunction() {
		super("clear", null);
	}

	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		return new Clear(environment);
	}
}