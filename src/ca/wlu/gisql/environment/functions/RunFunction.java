package ca.wlu.gisql.environment.functions;

import java.io.File;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.vm.Machine;

public final class RunFunction extends Function {

	public static final Function self = new RunFunction();

	private RunFunction() {
		super("run", "Runs commands in an external script", Type.StringType,
				Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		String filename = (String) parameters[0];
		RunFunctionListener listener = new RunFunctionListener(machine
				.getListener());
		ExpressionRunner runner = new ExpressionRunner(
				machine.getEnvironment(), listener);
		runner.run(new File(filename), null);
		return Unit.nil;
	}

}
