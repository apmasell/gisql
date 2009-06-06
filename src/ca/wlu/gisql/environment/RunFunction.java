package ca.wlu.gisql.environment;

import java.io.File;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.util.Function;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Unit;

public final class RunFunction extends Function {
	class Script extends Unit {
		private final UserEnvironment environment;

		private final File file;

		private Script(Environment environment, File file) {
			this.environment = (UserEnvironment) environment;
			this.file = file;
		}

		public boolean prepare() {
			if (!super.prepare())
				return false;
			return EnvironmentUtils.runFile(environment, file);

		}
	}

	public static final Parseable descriptor = new RunFunction();

	private RunFunction() {
		super("run", new Function.Parameter[] { new Function.QuotedString(
				"filename") });
	}

	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		File file = new File((String) params.get(0));
		if (file.canRead()) {
			return new Script(environment, file);
		} else {
			return null;
		}
	}
}