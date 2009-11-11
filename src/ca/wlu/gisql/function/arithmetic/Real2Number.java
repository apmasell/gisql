package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public class Real2Number extends Function {
	public static final Function self = new Real2Number();

	private Real2Number() {
		super("r2n", "Converts a real number to an integral number.",
				Type.RealType, Type.NumberType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return ((Double) parameters[0]).longValue();
	}

}
