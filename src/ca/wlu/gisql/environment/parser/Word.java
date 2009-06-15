package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Word extends Token {
	private final Parser parser;

	private final String word;

	public Word(Parser parser, String word) {
		this.parser = parser;
		this.word = word;
	}

	boolean parse(int level, List<AstNode> results) {
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