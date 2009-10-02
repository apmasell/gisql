package ca.wlu.gisql.environment.functions;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.vm.Machine;

public final class ClearFunction extends Function {
	public static final Function self = new ClearFunction();

	private ClearFunction() {
		super("clear", "Undefines all variables", Type.UnitType, Type.UnitType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		machine.getEnvironment().clear();
		return Unit.nil;
	}

}
