package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Real2Number extends Function {

	public Real2Number(ExpressionRunner runner) {
		super(runner, "r2n", "Converts a real number to an integral number.",
				Type.RealType, Type.NumberType);
	}

	@Override
	public Object run(Object... parameters) {
		return ((Double) parameters[0]).longValue();
	}

}
