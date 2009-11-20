package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class ClearFunction extends Function {

	public ClearFunction(ExpressionRunner runner) {
		super(runner, "clear", "Undefines all variables", Type.UnitType,
				Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		runner.getEnvironment().clear();
		return Unit.nil;
	}

}
