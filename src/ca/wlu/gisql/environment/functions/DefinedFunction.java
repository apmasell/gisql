package ca.wlu.gisql.environment.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class DefinedFunction extends Function {

	public DefinedFunction(ExpressionRunner runner) {
		super(runner, "ls", "List defined variables", Type.UnitType,
				new ListType(Type.StringType));
	}

	@Override
	public Object run(Object... parameters) {
		List<String> definedVariables = new ArrayList<String>();
		for (Entry<String, Object> item : runner.getEnvironment()) {
			definedVariables.add(item.getKey());
		}
		return definedVariables;
	}

}
