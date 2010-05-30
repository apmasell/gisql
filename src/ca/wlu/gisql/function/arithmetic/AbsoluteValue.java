package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class AbsoluteValue extends Function {

	public AbsoluteValue(ExpressionRunner runner) {
		super(runner, "abs", "Take the absolute value of a number.",
				Type.NumberType, Type.NumberType);
	}

	@Override
	public Object run(Object... parameters) {
		return Math.abs((Long) parameters[0]);
	}

}
