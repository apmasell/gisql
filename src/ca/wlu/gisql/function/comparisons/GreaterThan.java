package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class GreaterThan extends NumericComparison {
	public GreaterThan(ExpressionRunner runner) {
		super(runner, "gt");
	}

	@Override
	public boolean compare(int difference) {
		return difference > 0;
	}
}