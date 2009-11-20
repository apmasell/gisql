package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class Equal extends NumericComparison {
	public Equal(ExpressionRunner runner) {
		super(runner, "eq");
	}

	@Override
	public boolean compare(int difference) {
		return difference == 0;
	}
}