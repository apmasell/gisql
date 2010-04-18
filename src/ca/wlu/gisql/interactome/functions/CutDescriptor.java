package ca.wlu.gisql.interactome.functions;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenReal;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

final class CutDescriptor extends Parseable {
	private final Token[] tokens = new Token[] { TokenReal.self,
			TokenMatchCharacter.get('}') };

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new AstApplication(new AstName("cut"), params.get(0), params
				.get(1));
	}

	@Override
	protected String getInfo() {
		return "Cut-off (Ax|x>c)";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '{' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}
}
