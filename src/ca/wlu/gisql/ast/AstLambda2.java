package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.Instruction;

public class AstLambda2 extends AstNode {

	private final AstNode expression;

	private final Type resulttype = new TypeVariable();

	private final Type type;

	private final AstParameter variable;

	public AstLambda2(AstParameter variable, AstNode expression) {
		this.variable = variable;
		this.expression = expression;
		type = new ArrowType(variable.type, resulttype);
	}

	@Override
	protected int getNeededParameterCount() {
		return expression.getNeededParameterCount() + 1;
	}

	public int getPrecedence() {
		return Parser.PREC_FUNCTION;
	}

	@Override
	public Type getType() {
		return type;
	}

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
