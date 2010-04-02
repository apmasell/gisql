package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.MaskedEnvironment;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 1 representation of a lambda expression. There will be cleaned during
 * resolution and replaced with phase 2 lambda expressions.
 */
public class AstLambda1 extends AstNode {
	private final AstNode expression;

	final String name;

	public AstLambda1(String variable, AstNode expression) {
		name = variable;
		this.expression = expression;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		throw new IllegalStateException("AstLambda1 must be cleaned.");

	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean renderSelf(Rendering program, int depth) {
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
	 * {@link AstLambdaParameter}. Then encapsulate the result in a phase 2
	 * lambda.
	 */
	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		MaskedEnvironment<AstLambdaParameter> maskedenvironment = new MaskedEnvironment<AstLambdaParameter>(
				new AstLambdaParameter(name), environment);
		AstNode resultexpression = expression.resolve(runner, context,
				maskedenvironment);
		if (resultexpression == null) {
			return null;
		} else {
			return new AstLambda2(maskedenvironment.getVariable(),
					resultexpression);
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
