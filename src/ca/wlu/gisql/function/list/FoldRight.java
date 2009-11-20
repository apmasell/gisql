package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public class FoldRight extends Function {
	private static final TypeVariable a = new TypeVariable();

	private static final TypeVariable b = new TypeVariable();

	/* From Haskell: foldr :: (a -> b -> b) -> b -> [a] -> b */

	public FoldRight(ExpressionRunner runner) {
		super(runner, "foldr", "Right-recursive fold/reduce", new ArrowType(a,
				b, b), b, new ListType(a), b);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		GenericFunction function = (GenericFunction) parameters[0];
		Object right = parameters[1];
		List input = (List) parameters[2];
		for (int index = input.size() - 1; index >= 0; index--) {
			Object left = input.get(index);
			right = function.run(left, right);
		}
		return right;
	}
}
