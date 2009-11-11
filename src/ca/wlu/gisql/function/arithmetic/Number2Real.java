package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public class Number2Real extends Function {
	public static final Function self = new Number2Real();

	private Number2Real() {
		super("n2r", "Converts an integral number to a real number.",
				Type.NumberType, Type.RealType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return ((Long) parameters[0]).doubleValue();
	}

}
