package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.Instruction;

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

	/**
	 * This node will consume a parameter, so we incremented the needed
	 * parameter count. If our child node is also a lambda node, we should
	 * recuse. Otherwise, the type checker has ensured that it does not require
	 * any parameters, so recursion must stop.
	 */
	@Override
	protected int getNeededParameterCount() {
		return (expression instanceof AstLambda2 ? expression
				.getNeededParameterCount() : 0) + 1;
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
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (depth > 0) {
			debrujin++;
			variable.debrujin = debrujin;

			return program.instructions.add(Instruction.PushVariable)
					&& expression.render(program, depth - 1, debrujin)
					&& program.instructions.add(Instruction.PopVariable);
		} else {
			return wrap(program, depth, debrujin);
		}
	}

	@Override
	public void resetType() {
		expression.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
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
