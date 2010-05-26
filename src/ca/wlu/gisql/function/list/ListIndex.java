package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class ListIndex extends Function {

	private static TypeVariable a = new TypeVariable();

	public ListIndex(ExpressionRunner runner) {
		super(runner, "index", "Returns an arbitrary item from a list",
				new ListType(a), Type.NumberType, new MaybeType(a));
	}

	@Override
	public Object run(Object... parameters) {
		List<?> list = (List<?>) parameters[0];
		int index = ((Long) parameters[1]).intValue();
		if (index < 0) {
			index += list.size() + 1;
		}
		return index > 0 && index <= list.size() ? list.get(index - 1) : null;
	}
}
