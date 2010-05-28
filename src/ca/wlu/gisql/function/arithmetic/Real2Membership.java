package ca.wlu.gisql.function.arithmetic;

import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class Real2Membership extends Function {

	public Real2Membership(ExpressionRunner runner) {
		super(runner, "r2m",
				"Converts a real number to an membership, if it is in range.",
				Type.RealType, new MaybeType(Type.MembershipType));
	}

	@Override
	public Object run(Object... parameters) {
		double value = (Double) parameters[0];
		return (value >= 0 && value <= 1 ? value : null);
	}

}
