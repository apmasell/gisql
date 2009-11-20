package ca.wlu.gisql.function.list;

import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class ListLength extends Function {

	public ListLength(ExpressionRunner runner) {
		super(runner, "length", "Returns the length of a list", new ListType(
				new TypeVariable()), Type.NumberType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		return (long) ((List) parameters[0]).size();
	}

}
