package ca.wlu.gisql.interactome.logic;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** This is a special syntax parseable to handle complemented/negated sets. */
public class Complement extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> descriptor = new Complement();

	private Complement() {
		super(TokenExpressionChild.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode interactome = params.get(0);

		return AstLogic.makeNegation(interactome);
	}

	@Override
	protected String getInfo() {
		return "Complement (1-Ax)";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '!', '¬' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.UnaryPrefix;
	}

}
