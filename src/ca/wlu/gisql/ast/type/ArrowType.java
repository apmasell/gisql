package ca.wlu.gisql.ast.type;

import java.util.List;
import java.util.Map;

import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.ast.util.Rendering;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** The query language type of a function. */
public class ArrowType extends Type {
	private final Type operand;
	private final Type result;

	/**
	 * Convenience constructor to create a function with type "α → β → γ → ..."
	 * in one step, rather than "α → (β → (γ → ...))".
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
	public boolean equals(Object obj) {
		if (obj instanceof ArrowType) {
			ArrowType that = (ArrowType) obj;
			return operand.equals(that.operand) && result.equals(that.result);

		}
		return false;
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
	public Class<?> getRootJavaType() {
		return GenericFunction.class;
	}

	@Override
	public int hashCode() {
		return operand.hashCode() * 37 + result.hashCode();
	}

	@Override
	protected boolean occurs(Type needle) {
		return operand.occurs(needle) || result.occurs(needle);
	}

	@Override
	public boolean render(Rendering rendering, int depth) {
		return rendering.hP(result)
				&& rendering.hP(operand)
				&& rendering.pRg$hO_CreateObject(ArrowType.class
						.getConstructors()[1]);
	}

	@Override
	public void reset() {
		operand.reset();
		result.reset();
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
	 * type.
	 */
	@Override
	public boolean validate(Object value) {
		return value instanceof GenericFunction
				&& ((GenericFunction) value).getType().canUnify(this);
	}

}