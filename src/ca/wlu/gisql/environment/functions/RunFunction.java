package ca.wlu.gisql.environment.functions;

import java.io.File;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class RunFunction extends Function {

	public RunFunction(ExpressionRunner runner) {
		super(runner, "run", "Runs commands in an external script",
				Type.StringType, Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		String filename = (String) parameters[0];
		RunFunctionListener listener = new RunFunctionListener(runner
				.getListener());
		ExpressionRunner runner = new ExpressionRunner(this.runner
				.getEnvironment(), listener);
		runner.run(new File(filename), null);
		return Unit.nil;
	}

}
