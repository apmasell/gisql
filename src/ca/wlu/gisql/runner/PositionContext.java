package ca.wlu.gisql.runner;

/** The context of a specific character offset in a line. */
public class PositionContext extends ExpressionContext {
	private final LineContext parent;
	private final int position;

	public PositionContext(LineContext parent, int position) {
		super();
		this.parent = parent;
		this.position = position;
	}

	public LineContext getParent() {
		return parent;
	}

	public int getPosition() {
		return position;
	}

}
