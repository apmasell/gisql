package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;

public final class OutputFunction extends Function {
	class SetOutput extends Unit {
		private final Environment environment;

		private final String filename;

		private SetOutput(Environment environment, String filename) {
			this.environment = environment;
			this.filename = filename;
		}

		public boolean prepare() {
			if (environment instanceof UserEnvironment) {
				((UserEnvironment) environment).setOutput(filename);
				return super.prepare();
			} else {
				return false;
			}
		}
	}

	public static final Parseable descriptor = new OutputFunction();

	private OutputFunction() {
		super("output", new Function.Parameter[] { new Function.QuotedString(
				"filename") });
	}

	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		return new SetOutput(environment, (String) params.get(0));
	}
}