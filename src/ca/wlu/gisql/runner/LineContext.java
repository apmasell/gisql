package ca.wlu.gisql.runner;

/** The context of a line of query langauge. */
public abstract class LineContext extends ExpressionContext {

	public PositionContext getContextForPosition(int index) {
		return new PositionContext(this, index);
	}

	public abstract String getLine();

	public abstract String getSource();
}
