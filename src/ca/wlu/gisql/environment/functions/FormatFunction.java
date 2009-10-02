package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.interactome.output.FileFormat;
import ca.wlu.gisql.vm.Machine;

public class FormatFunction extends Function {
	public static final Function self = new FormatFunction();

	private FormatFunction() {
		super("format", "Changes file output format", Type.FormatType,
				Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		machine.getEnvironment().setFormat((FileFormat) parameters[0]);
		return Unit.nil;
	}

}
