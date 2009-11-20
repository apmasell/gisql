package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class OutputFileFunction extends Function {

	public OutputFileFunction(ExpressionRunner runner) {
		super(runner, "outputfile", "Changes output file", Type.StringType,
				Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		runner.getEnvironment().setOutput((String) parameters[0]);
		return Unit.nil;
	}

}
