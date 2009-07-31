package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Maybe extends Token {
	private final Token child;

	public Maybe(Token child) {
		super();
		this.child = child;
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		int errorposition = parser.error.size();
		if (child.parse(parser, level, results))
			return true;
		results.add(null);
		parser.position = oldposition;
		parser.error.setSize(errorposition);
		return true;
	}
}