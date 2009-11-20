package ca.wlu.gisql.ast;

import java.util.Set;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 1 representation of a recursive function. There will be cleaned during
 * resolution and replaced with phase 2 fixed-point expressions.
 */
public class AstFixedPoint1 extends AstNode {
	private class MaskedEnvironment implements ResolutionEnvironment {
		private final ResolutionEnvironment parent;
		private boolean used = false;
		private final AstFixedPointParameter variable;

		private MaskedEnvironment(ResolutionEnvironment parent) {
			this.parent = parent;
			variable = new AstFixedPointParameter(name);
		}

		@Override
		public Environment getEnvironment() {
			return parent.getEnvironment();
		}

		@Override
		public AstNode lookup(String name) {
			if (AstFixedPoint1.this.name.equals(name)) {
				used = true;
				return variable;
			} else {
				return parent.lookup(name);
			}
		}

	}

	private final AstNode expression;

	private final String name;

	public AstFixedPoint1(String variable, AstNode expression) {
		name = variable;
		this.expression = expression;
	}

	@Override
	protected void freeVariables(Set<String> variables) {
		throw new IllegalStateException("AstFixedPoint1 must be cleaned.");

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
				"AstFixedPoint1 must be cleaned before rendering.");
	}

	@Override
	public void resetType() {
		throw new IllegalStateException(
				"AstFixedPoint1 must be cleaned before typing.");
	}

	/**
	 * Set up a fake environment where the variable name is mapped to a
	 * {@link AstFixedPointParameter}. Then encapsulate the result in a phase 2
	 * fixed-point. If the recursion is never used, then the fixed-point node is
	 * stripped from the parse tree.
	 */
	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		MaskedEnvironment maskedenvironment = new MaskedEnvironment(environment);
		AstNode resultexpression = expression.resolve(runner, context,
				maskedenvironment);
		if (resultexpression == null) {
			return null;
		} else if (maskedenvironment.used) {
			return new AstFixedPoint2(maskedenvironment.variable,
					resultexpression);
		} else {
			return resultexpression;
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("($");
		print.print(name);
		print.print(' ');
		print.print(expression);
		print.print(')');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		throw new IllegalStateException(
				"AstFixedPoint1 must be cleaned before typing.");
	}
}
