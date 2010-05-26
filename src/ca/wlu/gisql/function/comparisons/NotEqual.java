package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class NotEqual extends Function {

	private static final TypeVariable a = new TypeVariable(TypeClass.Equalable);

	public NotEqual(ExpressionRunner runner) {
		super(runner, "ne", "Compares to things for inequality.", a, a,
				Type.BooleanType);
	}

	@Override
	public Object run(Object... parameters) {
		return !parameters[0].equals(parameters[1]);
	}
}