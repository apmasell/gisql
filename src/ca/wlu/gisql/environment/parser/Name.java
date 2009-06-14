package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Name extends Token {

	private final Parser parser;

	private String result;

	public Name(Parser parser) {
		this.parser = parser;
	}

	public String getResult() {
		return result;
	}

	boolean parse(int level, List<Object> results) {
		int oldposition = this.parser.position;
		String name = this.parser.parseName();
		if (name == null) {
			this.parser.error.push("Expected name missing. Position: "
					+ oldposition);
			return false;
		}
		result = name;
		results.add(name);
		return true;
	}

}