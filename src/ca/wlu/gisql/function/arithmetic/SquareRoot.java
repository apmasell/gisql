package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class SquareRoot extends Function {

	public SquareRoot(ExpressionRunner runner) {
		super(runner, "sqrt", "Take the square root of a number.",
				Type.RealType, Type.RealType);
	}

	@Override
	public Object run(Object... parameters) {
		return Math.sqrt((Double) parameters[0]);
	}

}
