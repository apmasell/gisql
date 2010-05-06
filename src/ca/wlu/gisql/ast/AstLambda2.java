package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Phase 2 representation of a lambda expression where the parameter is
 * encapsulated as an {@link AstParameter}.
 */
public class AstLambda2 extends AstNode {

	private final AstNode expression;

	private final Type type;

	private final AstParameter variable;

	public AstLambda2(AstParameter variable, AstNode expression) {
		this.variable = variable;
		this.expression = expression;
		type = new ArrowType(variable.type, expression.getType());
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		expression.freeVariables(variables);
		variables.remove(variable.variableInformation);
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Type getType() {
		return type;
	}

	/**
	 * A lambda expression move a variable to from the operand stack to the
	 * variable stack, run the inner code, then restores the variable stack.
	 */
	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		return program.pPg()
				&& program.hR_CreateLocal(variable.name, variable.getType()
						.getRootJavaType())
				&& expression.render(program, depth - 1)
				&& program.pR(variable.name);
	}

	@Override
	public void resetType() {
		variable.resetType();
		expression.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
		AstNode resultexpression = expression.resolve(runner, context,
				environment);
		if (resultexpression == null) {
			return null;
		} else {
			return new AstLambda2(variable, resultexpression);
		}
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		print.print("('");
		print.print(variable.name);
		print.print(' ');
		print.print(expression);
		print.print(')');
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		return expression.type(runner, context);
	}
}
