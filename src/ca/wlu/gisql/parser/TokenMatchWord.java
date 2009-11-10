package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

/**
 * Find exactly some word in the input. The word is not included in the results.
 */
public class TokenMatchWord extends TokenName {
	private final String word;

	public TokenMatchWord(String word) {
		super();
		this.word = word;
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
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