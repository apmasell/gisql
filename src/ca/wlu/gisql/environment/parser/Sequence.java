package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Sequence extends Token {

	private final Token first;

	private final Token second;

	public Sequence(final Token first, final Token second) {
		super();
		this.first = first;
		this.second = second;
	}

	boolean parse(int level, List<AstNode> results) {
		return first.parse(level, results) && second.parse(level, results);
	}

}
