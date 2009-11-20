package ca.wlu.gisql.function.list;

import java.util.ArrayList;
import java.util.List;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Flatten extends Function {

	private static final ListType alist = new ListType(new TypeVariable());

	public Flatten(ExpressionRunner runner) {
		super(runner, "flatten", "Flattens nested lists.", new ListType(alist),
				alist);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object run(Object... parameters) {
		List result = new ArrayList();
		for (List list : (List<List>) parameters[0]) {
			result.addAll(list);
		}
		return result;
	}

}
