package ca.wlu.gisql.function.pair;

import java.util.Map.Entry;

import ca.wlu.gisql.ast.type.PairType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.util.Function;
import ca.wlu.gisql.runner.ExpressionRunner;

public class PairKey extends Function {

	private static final Type a = new TypeVariable();
	private static final Type b = new TypeVariable();

	public PairKey(ExpressionRunner runner) {
		super(runner, "right", "Get the second entry in a pair", new PairType(
				a, b), b);
	}

	@Override
	public Object run(Object... parameters) {
		return ((Entry<?, ?>) parameters[0]).getValue();
	}

}
