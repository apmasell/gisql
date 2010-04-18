package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.ast.typeclasses.TypeClass;
import ca.wlu.gisql.util.Precedence;

/** Parser a decimal number. It cannot include an exponent. */
public class TokenReal extends Token {
	public static final TokenReal self = new TokenReal();

	private TokenReal() {
		super();
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		parser.mark();
		parser.consumeWhitespace();
		while (parser.hasMore() && Character.isDigit(parser.peek())) {
			parser.next();
		}
		if (parser.hasMore() && parser.peek() == '.') {
			parser.next();
			while (parser.hasMore() && Character.isDigit(parser.peek())) {
				parser.next();
			}
		} else {
			parser.clearMark();
			return false;
		}

		String string = parser.stringFromMark();
		try {
			double value = Double.parseDouble(string);
			Type type = Type.MembershipType.validate(value) ? new TypeVariable(
					TypeClass.Fractional) : Type.RealType;
			results.add(new AstLiteral(type, value));
			return true;
		} catch (NumberFormatException e) {
			parser.pushError("Failed to parse double.");
			return false;
		}
	}

	@Override
	public String toString() {
		return "<double>";
	}
}