package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The {@link AstNode} of an unresolved symbol. */
public class AstName extends AstNode {

	private final String name;

	public AstName(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	protected int getNeededParameterCount() {
		throw new IllegalStateException(
				"Names should be cleaned from the parse tree.");
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		throw new IllegalStateException(
				"Names should be cleaned from the parse tree.");
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		AstNode self = environment.getVariable(name);
		if (self == null) {
			runner.appendResolutionError("Undefined name", this, context);
			return null;
		}
		return self.resolve(runner, context, environment);
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print(name);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		throw new IllegalStateException(
				"Names should be cleaned from the parse tree.");
	}
}
