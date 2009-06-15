package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Maybe extends Token {
	private final Token child;

	private final Parser parser;

	public Maybe(Parser parser, Token child) {
		super();
		this.parser = parser;
		this.child = child;
	}

	boolean parse(int level, List<AstNode> results) {
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