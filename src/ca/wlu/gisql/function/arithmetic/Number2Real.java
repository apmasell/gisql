package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Number2Real extends Function {

	public Number2Real(ExpressionRunner runner) {
		super(runner, "n2r", "Converts an integral number to a real number.",
				Type.NumberType, Type.RealType);
	}

	@Override
	public Object run(Object... parameters) {
		return ((Long) parameters[0]).doubleValue();
	}

}
