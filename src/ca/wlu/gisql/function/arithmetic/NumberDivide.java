package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class NumberDivide extends Function {

	public NumberDivide(ExpressionRunner runner) {
		super(runner, "div", "Divides two numbers.", Type.NumberType,
				Type.NumberType, Type.NumberType);
	}

	@Override
	public Object run(Object... parameters) {
		long denominator = (Long) parameters[1];
		if (denominator == 0) {
			return Long.MAX_VALUE;
		} else {
			return (Long) parameters[0] / denominator;
		}
	}

}
