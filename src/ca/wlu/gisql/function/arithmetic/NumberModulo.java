package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class NumberModulo extends Function {

	public NumberModulo(ExpressionRunner runner) {
		super(runner, "mod", "Find the remainder after dividing two numbers.",
				Type.NumberType, Type.NumberType, Type.NumberType);
	}

	@Override
	public Object run(Object... parameters) {
		return (Long) parameters[0] % (Long) parameters[1];
	}

}
