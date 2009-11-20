package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class LessThanOrEqualTo extends NumericComparison {
	public LessThanOrEqualTo(ExpressionRunner runner) {
		super(runner, "le");
	}

	@Override
	public boolean compare(int difference) {
		return difference <= 0;
	}
}