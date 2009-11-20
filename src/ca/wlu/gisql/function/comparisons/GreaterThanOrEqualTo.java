package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class GreaterThanOrEqualTo extends NumericComparison {
	public GreaterThanOrEqualTo(ExpressionRunner runner) {
		super(runner, "ge");
	}

	@Override
	public boolean compare(int difference) {
		return difference >= 0;
	}
}