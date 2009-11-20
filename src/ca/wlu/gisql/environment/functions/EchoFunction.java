package ca.wlu.gisql.environment.functions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public final class EchoFunction extends Function {

	private static final Logger log = Logger.getLogger(EchoFunction.class);

	public EchoFunction(ExpressionRunner runner) {
		super(runner, "echo", "Prints something to the screen",
				new TypeVariable(), Type.UnitType);
	}

	@Override
	public Object run(Object... parameters) {
		String filename = runner.getEnvironment().getOutput();
		PrintStream print;
		try {
			print = filename == null ? System.out : new PrintStream(
					new FileOutputStream(filename, true));
			print.println(parameters[0]);
		} catch (IOException e) {
			log.error("Failed to echo.", e);
		}
		return Unit.nil;
	}
}
