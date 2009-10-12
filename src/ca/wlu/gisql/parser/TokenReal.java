package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;

public class TokenReal extends Token {
	public static final TokenReal self = new TokenReal();

	private TokenReal() {
		super();
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		parser.consumeWhitespace();
		while (parser.position < parser.input.length()
				&& Character.isDigit(parser.input.charAt(parser.position))) {
			parser.position++;
		}
		if (parser.position < parser.input.length()
				&& parser.input.charAt(parser.position) == '.') {
			parser.position++;
			while (parser.position < parser.input.length()
					&& Character.isDigit(parser.input.charAt(parser.position))) {
				parser.position++;
			}
		} else {
			return false;
		}

		try {
			double value = Double.parseDouble(parser.input.substring(
					oldposition, parser.position));
			Type type = Type.MembershipType.validate(value) ? new TypeVariable(
					TypeClass.Fractional) : Type.RealType;
			results.add(new AstLiteral(type, value));
			return true;
		} catch (NumberFormatException e) {
			parser.pushError("Failed to parse double.");
			return false;
		}
	}
}