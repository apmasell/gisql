package ca.wlu.gisql.function.comparisons;

import ca.wlu.gisql.runner.ExpressionRunner;

public final class NotEqual extends NumericComparison {
	public NotEqual(ExpressionRunner runner) {
		super(runner, "ne");
	}

	@Override
	public boolean compare(int difference) {
		return difference != 0;
	}
}