package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class AstApplication extends AstNode {

	private final AstNode operand;
	private final Type operandtype = new TypeVariable();

	private final AstNode operator;

	private final Type returntype = new TypeVariable();

	public AstApplication(AstNode... arguments) {
		if (arguments.length < 2) {
			throw new IllegalArgumentException("Need 2 or more arguments.");
		}

		AstNode operator = arguments[0];
		AstNode operand = arguments[1];
		for (int i = 2; i < arguments.length; i++) {
			operator = new AstApplication(operator, operand);
			operand = arguments[i];
		}
		this.operator = operator;
		this.operand = operand;
	}

	public AstApplication(AstNode operator, AstNode operand) {
		super();
		this.operator = operator;
		this.operand = operand;
	}

	@Override
	protected int getNeededParameterCount() {
		return operator.getNeededParameterCount() - 1;
	}

	public int getPrecedence() {
		return Parser.PREC_APPLICATION;
	}

	@Override
	public Type getType() {
		return returntype;
	}

	@Override
	public boolean render(ProgramRoutine program, int depth, int debrujin) {
		if (operator.getNeededParameterCount() - depth > 1) {
			return wrap(program, depth, debrujin);
		} else {
			return operand.render(program, 0, debrujin)
					&& operator.render(program, depth + 1, debrujin);
		}
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			Environment environment) {
		AstNode operator = this.operator.resolve(runner, context, environment);
		if (operator == null) {
			return null;
		}
		AstNode operand = this.operand.resolve(runner, context, environment);
		if (operand == null) {
			return null;
		}

		return new AstApplication(operator, operand);
	}

	public void show(ShowablePrintWriter<AstNode> print) {
		boolean brackets = operand instanceof AstApplication;
		print.print(operator);
		print.print(' ');
		if (brackets) {
			print.print('(');
		}
		print.print(operand);
		if (brackets) {
			print.print(')');
		}
	}

	@Override
	public boolean type(ExpressionRunner runner, ExpressionContext context) {
		if (!(operator.type(runner, context) && operand.type(runner, context))) {
			return false;
		}
		Type operatortype = new ArrowType(operandtype, returntype);
		if (!operator.getType().unify(operatortype)) {
			runner.appendTypeError(operator.getType(), operatortype, this,
					context);
			return false;
		}

		if (!operand.getType().unify(operandtype)) {
			runner.appendTypeError(operand.getType(), operandtype, this,
					context);
			return false;
		}

		return true;
	}
}
