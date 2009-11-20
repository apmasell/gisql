package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class LessThan extends NumericComparison {
	public LessThan(ExpressionRunner runner) {
		super(runner, "lt");
	}

	@Override
	public boolean compare(int difference) {
		return difference < 0;
	}
}