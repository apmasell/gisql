package ca.wlu.gisql.ast.type;

import java.util.List;

import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.Program;

public class ArrowType extends Type {
	private final Type operand;
	private final Type result;

	public ArrowType(Type... arguments) {
		if (arguments.length < 2) {
			throw new IllegalArgumentException("Need 2 or more arguments.");
		}

		operand = arguments[0];
		Type result = arguments[arguments.length - 1];
		for (int index = arguments.length - 2; index > 0; index--) {
			result = new ArrowType(arguments[index], result);
		}
		this.result = result;
	}

	public ArrowType(Type operand, Type result) {
		if (operand == null || result == null) {
			throw new IllegalArgumentException(
					"Arrow types cannot include null.");
		}
		this.operand = operand;
		this.result = result;
	}

	@Override
	public boolean canUnify(Object obj) {
		return obj instanceof ArrowType ? operand
				.canUnify(((ArrowType) obj).operand)
				&& result.canUnify(((ArrowType) obj).result) : super
				.canUnify(obj);
	}

	@Override
	protected Type freshen(Type needle, Type replacement) {
		Type freshOperand = operand.freshen(needle, replacement);
		Type freshResult = result.freshen(needle, replacement);
		if (freshOperand == operand && freshResult == result) {
			return this;
		} else {
			return new ArrowType(freshOperand, freshResult);
		}
	}

	@Override
	public int getArrowDepth() {
		return 1 + result.getArrowDepth();
	}

	@Override
	protected boolean occurs(Type needle) {
		return operand.occurs(needle) || result.occurs(needle);
	}

	public void show(ShowablePrintWriter<List<TypeVariable>> print) {
		boolean brackets = operand.getArrowDepth() > 0;
		if (brackets) {
			print.print("(");
		}
		print.print(operand);
		if (brackets) {
			print.print(")");
		}
		print.print(" → ");
		print.print(result);
	}

	@Override
	public boolean unify(Type that) {
		if (this == that) {
			return true;
		} else if (that instanceof ArrowType) {
			ArrowType other = (ArrowType) that;
			return operand.unify(other.operand) && result.unify(other.result);
		} else {
			return super.unify(that);
		}
	}

	@Override
	public boolean validate(Object value) {
		/* WARNING: This does not check the actual type really. */
		return value instanceof Program;
	}

}