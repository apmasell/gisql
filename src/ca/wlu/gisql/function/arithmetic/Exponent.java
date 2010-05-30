package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Exponent extends Function {

	public Exponent(ExpressionRunner runner) {
		super(runner, "exp", "Raise e to the power of a number.",
				Type.RealType, Type.RealType);
	}

	@Override
	public Object run(Object... parameters) {
		return Math.exp((Double) parameters[0]);
	}

}
