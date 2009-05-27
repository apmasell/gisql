package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Word extends NextTask {
	private final Parser parser;

	private final String word;

	public Word(Parser parser, String word) {
		this.parser = parser;
		this.word = word;
	}

	boolean parse(int level, List<Object> results) {
		int oldposition = this.parser.position;
		String name = this.parser.parseName();
		if (name == null || !word.equals(name)) {
			this.parser.error.push("Expected " + word + " missing. Position: "
					+ oldposition);
			return false;
		}
		return true;
	}

}