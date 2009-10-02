package ca.wlu.gisql.environment.functions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.vm.Machine;

public final class EchoFunction extends Function {

	private static final Logger log = Logger.getLogger(EchoFunction.class);

	public static final Function self = new EchoFunction();

	private EchoFunction() {
		super("echo", "Prints something to the screen", new TypeVariable(),
				Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		String filename = machine.getEnvironment().getOutput();
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
