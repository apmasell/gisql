package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.runner.ExpressionRunner;

public class FormatFunction extends Function {

	public FormatFunction(ExpressionRunner runner) {
		super(runner, "format", "Changes file output format", Type.FormatType,
				Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		runner.getEnvironment().setFormat((FileFormat) parameters[0]);
		return Unit.nil;
	}

}
