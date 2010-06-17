package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class SpaceShip extends Function {
	private final static Type a = new TypeVariable(TypeClass.Comparable);

	public SpaceShip(ExpressionRunner runner) {
		super(
				runner,
				"cmp",
				"Compare two values and returns 0 if they are the same, 1 if the first is greater than the second, and -1 if the second is greater than the first.",
				a, a, Type.NumberType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		return ((Comparable<Object>) parameters[0]).compareTo(parameters[1]);
	}
}