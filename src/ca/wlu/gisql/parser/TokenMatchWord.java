package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

public class TokenMatchWord extends TokenName {
	private final String word;

	public TokenMatchWord(String word) {
		super();
		this.word = word;
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		if (super.parse(parser, level, results)) {
			if (results.get(results.size() - 1).equals(word)) {
				return true;
			} else {
				results.remove(results.size() - 1);
			}
		}
		parser.pushError("Expected " + word + " missing.");
		return false;
	}

}