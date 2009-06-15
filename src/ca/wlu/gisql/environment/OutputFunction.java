package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;

public final class OutputFunction extends Function {
	class SetOutput extends AstVoid {
		private final Environment environment;

		private final String filename;

		private SetOutput(Environment environment, AstString filename) {
			this.environment = environment;
			this.filename = filename.getString();
		}

		public void execute() {
			if (environment instanceof UserEnvironment) {
				((UserEnvironment) environment).setOutput(filename);
			}
		}
	}

	public static final Parseable descriptor = new OutputFunction();

	private OutputFunction() {
		super("output", new Function.Parameter[] { new Function.QuotedString(
				"filename") });
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		return new SetOutput(environment, (AstString) params.get(0));
	}
}