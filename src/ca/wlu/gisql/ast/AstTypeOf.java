package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Special node that returns the type of its argument rather than the value of
 * the argument.
 */
public class AstTypeOf extends AstNode {
	private final AstNode parameter;

	public AstTypeOf(AstNode parameter) {
		super();
		this.parameter = parameter;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AstTypeOf) {
			return parameter.equals(((AstTypeOf) other).parameter);
		} else {
			return false;
		}
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		parameter.freeVariables(variables);
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return Type.StringType;
	}

	@Override
	public int hashCode() {
		return parameter.hashCode() * 227;
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.hO(parameter.getType().toString());
	}

	@Override
	public void resetType() {
		parameter.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode parameter = this.parameter
				.resolve(runner, context, environment);
		if (parameter == null) {
			return null;
		} else {
			return new AstTypeOf(parameter);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("# ");
		print.print(parameter);
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return parameter.type(runner, context);
	}
}
