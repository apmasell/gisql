package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Maybe extends NextTask {
	private final Parser parser;

	private final NextTask child;

	public Maybe(Parser parser, NextTask child) {
		super();
		this.parser = parser;
		this.child = child;
	}

	boolean parse(int level, List<Object> results) {
		int oldposition = this.parser.position;
		int errorposition = this.parser.error.size();
		if (child.parse(level, results))
			return true;
		results.add(null);
		this.parser.position = oldposition;
		this.parser.error.setSize(errorposition);
		return true;
	}
}