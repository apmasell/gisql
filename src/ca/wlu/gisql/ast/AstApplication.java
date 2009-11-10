package ca.wlu.gisql.ast;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The application of a function to an operand. (i.e., the node representing (f
 * x))
 */
public class AstApplication extends AstNode {

	private final AstNode operand;

	private final TypeVariable operandtype = new TypeVariable();

	private final AstNode operator;

	private final TypeVariable returntype = new TypeVariable();

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
		if (operator == null) {
			throw new IllegalArgumentException("Operator cannot be null.");
		}
		if (operand == null) {
			throw new IllegalArgumentException("Operand cannot be null.");
		}
		this.operator = operator;
		this.operand = operand;
	}

	/**
	 * This node is providing a parameter, so we decrement the number needed by
	 * our the operator.
	 */
	@Override
	protected int getNeededParameterCount() {
		return operator.getNeededParameterCount() - 1;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return returntype;
	}

	/**
	 * If there are an insufficient number of parameters, call
	 * {@link #wrap(ProgramRoutine, int, int)} to create closures. If not,
	 * render the operand, so that its value will be on the stack, then render
	 * the operator.
	 */
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
	public void resetType() {
		operandtype.reset();
		returntype.reset();
		operand.resetType();
		operator.resetType();
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

	/**
	 * Ensure that this node is of type β given the operator is of type (α → β)
	 * and the operand is of type α.
	 */
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
