package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Map extends Function {

	private static final TypeVariable a = new TypeVariable();
	private static final TypeVariable b = new TypeVariable();

	public Map(ExpressionRunner runner) {
		super(runner, "map", "Applies a function to all members of a list",
				new ArrowType(a, b), new ListType(a), new ListType(b));
	}

	@Override
	public Object run(Object... params) {
		GenericFunction function = (GenericFunction) params[0];
		List<?> input = (List<?>) params[1];
		List<Object> output = new ArrayList<Object>();
		for (Object object : input) {
			output.add(function.run(object));
		}
		return output;
	}

}
