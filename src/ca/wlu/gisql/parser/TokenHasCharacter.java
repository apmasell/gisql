package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;

public class TokenHasCharacter extends Token {
	private final char[] delimiters;

	public TokenHasCharacter(char... delimiters) {
		super();
		this.delimiters = delimiters;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		char codepoint = parser.peek();
		for (char delimiter : delimiters) {
			if (codepoint == delimiter) {
				parser.next();
				parser.consumeWhitespace();
				results.add(new AstLiteral(Type.BooleanType, true));
				return true;
			}
		}
		results.add(new AstLiteral(Type.BooleanType, false));
		return true;
	}

	@Override
	public String toString() {
		return "[" + new String(delimiters) + "]?";
	}

}
