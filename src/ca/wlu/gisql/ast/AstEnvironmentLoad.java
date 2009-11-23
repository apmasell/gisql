package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The {@link AstNode} of an known symbol in the environment. This will fetch a
 * symbol during execution.
 */
public class AstEnvironmentLoad extends AstNode {

	private final String name;
	private final Type type;

	public AstEnvironmentLoad(Function function) {
		this(function.toString(), function.getType());
	}

	public AstEnvironmentLoad(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		/*
		 * This variable is considered bound in the environment, even though, by
		 * classical reasoning, this would be considered a free variable.
		 */
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public boolean renderSelf(Rendering program, int depth) {
		return program.lEhO(name);
	}

	@Override
	public void resetType() {
		type.reset();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		return this;
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return true;
	}
}