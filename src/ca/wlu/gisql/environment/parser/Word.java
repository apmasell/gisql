package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Word extends Token {
	private final String word;

	public Word(String word) {
		this.word = word;
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		String name = parser.parseName();
		if (name == null || !word.equals(name)) {
			parser.error.push("Expected " + word + " missing. Position: "
					+ oldposition);
			return false;
		}
		return true;
	}

}