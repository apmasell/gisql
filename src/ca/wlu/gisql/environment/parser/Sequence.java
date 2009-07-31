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

	boolean parse(Parser parser, int level, List<AstNode> results) {
		return first.parse(parser, level, results)
				&& second.parse(parser, level, results);
	}

}
