package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

/**
 * Find exactly some word in the input. The word is not included in the results.
 */
public class TokenReservedWord extends Token {
	private final String word;

	public TokenReservedWord(String word) {
		super();
		this.word = word;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		reservedwords.add(word);
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		for (int index = 0; index < word.length(); index++) {
			if (!parser.hasMore() || word.charAt(index) != parser.read()) {
				parser.pushError("Expected " + word + " missing.");
				return false;
			}
		}
		return !parser.hasMore()
				|| !Character.isJavaIdentifierPart(parser.peek());
	}

	@Override
	public String toString() {
		return "'" + word + "'";
	}

}