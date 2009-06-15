package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.environment.parser.util.Function;

public final class ClearFunction extends Function {
	class Clear extends AstVoid {
		private final Environment environment;

		private Clear(Environment environment) {
			this.environment = environment;
		}

		public void execute() {
			environment.clear();
		}
	}

	public static final Parseable descriptor = new ClearFunction();

	private ClearFunction() {
		super("clear", null);
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		return new Clear(environment);
	}
}