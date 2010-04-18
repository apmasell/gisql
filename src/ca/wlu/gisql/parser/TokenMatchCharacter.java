package ca.wlu.gisql.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

/** Find exactly some character. The character is not included in the results. */
public class TokenMatchCharacter extends Token {
	private static final Map<Character, TokenMatchCharacter> literals = new HashMap<Character, TokenMatchCharacter>();

	public static TokenMatchCharacter get(char c) {
		TokenMatchCharacter literal = literals.get(c);
		if (literal == null) {
			literal = new TokenMatchCharacter(c);
			literals.put(c, literal);
		}
		return literal;
	}

	private final char c;

	private TokenMatchCharacter(char c) {
		super();
		this.c = c;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		parser.consumeWhitespace();
		if (parser.hasMore() && c == parser.peek()) {
			parser.next();
			return true;
		}
		parser.pushError("Expected '" + c + "' missing.");
		return false;
	}

	@Override
	public String toString() {
		return "\\" + c;
	}
}