package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Sequence extends NextTask {

	private final NextTask first;

	private final NextTask second;

	public Sequence(final NextTask first, final NextTask second) {
		super();
		this.first = first;
		this.second = second;
	}

	boolean parse(int level, List<Object> results) {
		return first.parse(level, results) && second.parse(level, results);
	}

}
