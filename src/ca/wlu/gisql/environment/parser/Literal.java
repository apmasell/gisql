package ca.wlu.gisql.environment.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Literal extends Token {
	private static final Map<Character, Literal> literals = new HashMap<Character, Literal>();

	public static Literal get(char c) {
		Literal literal = literals.get(c);
		if (literal == null) {
			literal = new Literal(c);
			literals.put(c, literal);
		}
		return literal;
	}

	private final char c;

	private Literal(char c) {
		super();
		this.c = c;
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		parser.consumeWhitespace();
		if (parser.position < parser.input.length()
				&& c == parser.input.charAt(parser.position)) {
			parser.position++;
			return true;
		}
		parser.error.push("Expected '" + c + "' missing. Position: "
				+ parser.position);
		return false;
	}

}