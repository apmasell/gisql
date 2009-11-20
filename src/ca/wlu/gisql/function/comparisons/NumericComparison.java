package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

abstract class NumericComparison extends Function {

	private static final Type anumber = new TypeVariable(TypeClass.Comparable);

	protected NumericComparison(ExpressionRunner runner, String name) {
		super(runner, name, "Compare to numbers in the obvious way.", anumber,
				anumber, Type.BooleanType);
	}

	public abstract boolean compare(int difference);

	@SuppressWarnings("unchecked")
	@Override
	public final Object run(Object... parameters) {
		return compare(((Comparable) parameters[0]).compareTo(parameters[1]));
	}
}
