package ca.wlu.gisql.runner;

public abstract class LineContext extends ExpressionContext {
	public PositionContext getContextForPosition(int index) {
		return new PositionContext(this, index);
	}

	public abstract String getLine();

	public abstract String getSource();
}
