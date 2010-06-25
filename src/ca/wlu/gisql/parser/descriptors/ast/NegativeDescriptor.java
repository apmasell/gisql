package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenNumber;
import ca.wlu.gisql.parser.TokenReal;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** This operator extracts the help information from a function and returns it. */
public class NegativeDescriptor extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> numberdescriptor = new NegativeDescriptor(
			TokenNumber.self);
	public static final Parseable<AstNode, Precedence> realdescriptor = new NegativeDescriptor(
			TokenReal.self);

	public static void helpFor(StringBuilder sb, String name, Type type,
			Object value) {
	}

	private NegativeDescriptor(Token<AstNode, Precedence> token) {
		super(token);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		AstLiteral number = (AstLiteral) params.get(0);
		if (number.getType() == Type.NumberType) {
			return new AstLiteral(Type.NumberType, -(Long) number.getValue());
		} else {
			return new AstLiteral(Type.RealType, -(Double) number.getValue());
		}
	}

	@Override
	protected String getInfo() {
		return "Negative value";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '-' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.UnaryPrefix;
	}

}
