package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public class FoldLeft extends Function {
	private static final TypeVariable a = new TypeVariable();

	private static final TypeVariable b = new TypeVariable();

	/* From Haskell: foldl :: (a -> b -> a) -> a -> [b] -> a */

	public FoldLeft(ExpressionRunner runner) {
		super(runner, "foldl", "Left-recursive fold/reduce", new ArrowType(a,
				b, a), a, new ListType(b), a);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run(Object... parameters) {
		GenericFunction function = (GenericFunction) parameters[0];
		Object left = parameters[1];
		List input = (List) parameters[2];
		for (Object right : input) {
			left = function.run(left, right);
		}
		return left;
	}
}
