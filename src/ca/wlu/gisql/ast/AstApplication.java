package ca.wlu.gisql.ast;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.Renderable;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.ast.util.ResolutionEnvironment;
import ca.wlu.gisql.ast.util.VariableInformation;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * The application of a function to an operand. (i.e., the node representing
 * <tt>(f x)</tt>)
 */
public class AstApplication extends AstNode {

	private final class MaybeOperand implements Renderable {
		private final Label label;

		private MaybeOperand(Label label) {
			this.label = label;
		}

		@Override
		public <C> boolean render(Rendering<C> program, int depth) {
			return operand.render(program, depth) && program.lOhO()
					&& program.jump(Opcodes.IFNULL, label);
		}
	}

	private final AstNode operand;

	private boolean operandmaybe = false;

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

	public AstApplication(Function function, AstNode... arguments) {
		if (arguments.length < 1) {
			throw new IllegalArgumentException("Need 2 or more arguments.");
		}

		AstNode operator = new AstNativeIndirect(new AstNativeGenericFunction(
				function));
		AstNode operand = arguments[0];
		for (int i = 1; i < arguments.length; i++) {
			operator = new AstApplication(operator, operand);
			operand = arguments[i];
		}
		this.operator = operator;
		this.operand = operand;
	}

	@Override
	protected void freeVariables(ListOrderedSet<VariableInformation> variables) {
		operand.freeVariables(variables);
		operator.freeVariables(variables);
	}

	@Override
	protected int getLeftDepth() {
		return operator.getLeftDepth() - 1;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Type getType() {
		return returntype;
	}

	@Override
	public <T> boolean renderSelf(Rendering<T> program, int depth) {
		final Label label = new Label();
		return program.hP(operandmaybe ? new MaybeOperand(label) : operand)

		&& operator.render(program, depth + 1) && program.mark(label);
	}

	@Override
	public void resetType() {
		returntype.reset();
		operand.resetType();
		operator.resetType();
	}

	@Override
	public AstNode resolve(ExpressionRunner runner, ExpressionContext context,
			ResolutionEnvironment environment) {
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
		print.print(operator, getPrecedence());
		print.print(' ');
		if (brackets) {
			print.print('(');
		}
		print.print(operand, getPrecedence());
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

		if (operator.getType().canUnify(
				new ArrowType(operand.getType(), returntype))) {
			operandmaybe = false;
			return operator.getType().unify(
					new ArrowType(operand.getType(), returntype));
		}

		if (operator.getType().canUnify(
				new ArrowType(new MaybeType(operand.getType()), returntype))) {
			operandmaybe = false;
			return operator.getType()
					.unify(
							new ArrowType(new MaybeType(operand.getType()),
									returntype));
		}

		Type t = new TypeVariable();
		Type r = new TypeVariable();
		if (operator.getType().unify(new ArrowType(t, r))
				&& operand.getType().unify(new MaybeType(t))
				&& returntype.unify(r.getTerminalMaybe())) {
			operandmaybe = true;
			return true;
		}

		runner.appendTypeError(operator.getType(), new ArrowType(operand
				.getType(), returntype), this, context);
		return false;
	}
}
