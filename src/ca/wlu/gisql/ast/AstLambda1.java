package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 1 representation of a lambda expression. There will be cleaned during
 * resolution and replaced with phase 2 lambda expressions.
 */
public class AstLambda1 extends AstNode {
	private class MaskedEnvironment extends Environment {
		private final AstParameter variable;

		private MaskedEnvironment(Environment parent) {
			super(parent, false, true);
			variable = new AstParameter(name);
			add(name, variable);
		}
	}

	private final AstNode expression;

	private final String name;

	public AstLambda1(String variable, AstNode expression) {
		name = variable;
		this.expression = expression;
	}

	@Override
	protected int getNeededParameterCount() {
		throw new IllegalStateException(
				"AstLambda1 must be cleaned before rendering.");
	}

	public int getPrecedence() {
		return Parser.PREC_FUNCTION;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		throw new IllegalStateException(
				"AstLambda1 must be cleaned before rendering.");
	}

	@Override
	public void resetType() {
		throw new IllegalStateException(
				"AstLambda1 must be cleaned before typing.");
	}

	/**
	 * Set up a fake environment where the variable name is mapped to a
	 * {@link AstParameter}. Then encapsulate the result in a phase 2 lambda.
	 */
	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		MaskedEnvironment maskedenvironment = new MaskedEnvironment(environment);
		AstNode resultexpression = expression.resolve(runner, context,
				maskedenvironment);
		if (resultexpression == null) {
			return null;
		} else {
			return new AstLambda2(maskedenvironment.variable, resultexpression);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("('");
		print.print(name);
		print.print(' ');
		print.print(expression);
		print.print(')');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		throw new IllegalStateException(
				"AstLambda1 must be cleaned before typing.");
	}
}
