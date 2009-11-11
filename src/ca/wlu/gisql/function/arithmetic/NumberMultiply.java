package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.vm.Machine;

public class NumberMultiply extends Function {
	public static final Function self = new NumberMultiply();

	private NumberMultiply() {
		super("mul", "Multiplies two numbers.", Type.NumberType,
				Type.NumberType, Type.NumberType);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return (Long) parameters[0] * (Long) parameters[1];
	}

}
