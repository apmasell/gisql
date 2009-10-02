package ca.wlu.gisql.function;

import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.vm.Machine;

public class Comparison extends Function {
	private static final TypeVariable a = new TypeVariable();
	public static final Function self = new Comparison();

	private Comparison() {
		super("if", "Makes a decision", Type.BooleanType, a, a, a);
	}

	@Override
	public Object run(Machine machine, Object... parameters) {
		return (Boolean) parameters[0] ? parameters[1] : parameters[2];
	}

}
