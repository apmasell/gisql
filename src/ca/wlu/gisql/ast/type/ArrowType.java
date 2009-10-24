package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;

import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.vm.Program;

/** The query language type of a function. */
public class ArrowType extends Type {
	private final Type operand;
	private final Type result;

	/**
	 * Convience constructor to create a function with type "α → β → γ → ..." in
	 * one step, rather than "α → (β → (γ → ...))".
	 */
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

	/**
	 * Create a type which takes a parameter of type <tt>operand</tt> and
	 * returns a result of type <tt>result</tt>.
	 */
	public ArrowType(Type operand, Type result) {
		if (operand == null || result == null) {
			throw new IllegalArgumentException(
					"Arrow types cannot include null.");
		}
		this.operand = operand;
		this.result = result;
	}

	@Override
	public boolean canUnify(Type othertype) {
		return othertype instanceof ArrowType ? operand
				.canUnify(((ArrowType) othertype).operand)
				&& result.canUnify(((ArrowType) othertype).result) : super
				.canUnify(othertype);
	}

	@Override
	protected Type freshen(Map<Type, Type> replacement) {
		Type freshOperand = operand.freshen(replacement);
		Type freshResult = result.freshen(replacement);
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

	/**
	 * This is supposed to check whether an object is a valid instance of this
	 * type. Since {@link Program} has no type information, this <b>does not
	 * check</b> the actual type really, only that it is a program.
	 */
	@Override
	public boolean validate(Object value) {
		return value instanceof Program;
	}

}