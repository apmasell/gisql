package ca.wlu.gisql.graph;

public class MaybeEdge {
	private final boolean present;

	public MaybeEdge(boolean present) {
		super();
		this.present = present;
	}

	public boolean isPresent() {
		return present;
	}

	@Override
	public String toString() {
		return present ? "-" : "✂";
	}

}
