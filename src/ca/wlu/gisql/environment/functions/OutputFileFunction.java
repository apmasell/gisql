package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.vm.Machine;

public final class OutputFileFunction extends Function {

	public static final Function self = new OutputFileFunction();

	private OutputFileFunction() {
		super("outputfile", "Changes output file", Type.StringType,
				Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		machine.getEnvironment().setOutput((String) parameters[0]);
		return Unit.nil;
	}

}
