package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.List;

public class ListOf extends NextTask {
	private final Parser parser;

	private final NextTask child;

	private final char delimiter;

	public ListOf(Parser parser, NextTask child, char delimiter) {
		super();
		this.parser = parser;
		this.child = child;
		this.delimiter = delimiter;
	}

	boolean parse(int level, List<Object> results) {
		List<Object> items = new ArrayList<Object>();

		if (!child.parse(level, items)) {
			return false;
		}

		this.parser.consumeWhitespace();
		while (this.parser.position < this.parser.input.length()) {
			if (this.parser.input.charAt(this.parser.position) == delimiter) {
				this.parser.position++;
				if (!child.parse(level, items)) {
					return false;
				}
			} else {
				results.add(items);
				return true;
			}
			this.parser.consumeWhitespace();
		}
		results.add(items);
		return true;
	}

}