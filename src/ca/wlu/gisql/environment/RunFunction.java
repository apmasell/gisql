package ca.wlu.gisql.environment;

import java.io.File;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;

public final class RunFunction extends Function {
	class Script extends AstVoid {
		private final UserEnvironment environment;

		private final File file;

		private Script(Environment environment, File file) {
			this.environment = (UserEnvironment) environment;
			this.file = file;
		}

		public void execute() {
			EnvironmentUtils.runFile(environment, file);
		}
	}

	public static final Parseable descriptor = new RunFunction();

	private RunFunction() {
		super("run", new Function.Parameter[] { new Function.QuotedString(
				"filename") });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		File file = new File(((AstString) params.get(0)).getString());
		if (file.canRead()) {
			return new Script(environment, file);
		} else {
			return null;
		}
	}
}