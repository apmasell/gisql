package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNativeConstructor;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.function.pair.Pair;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Parsers the convenience syntax for pairs. A pair (pair x y), may be written
 * has (x*y).
 */
public class PairDescriptor extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> descriptor = new PairDescriptor();

	private PairDescriptor() {
		super(TokenExpressionRight.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode left = params.get(0);
		AstNode right = params.get(1);
		return new AstApplication(new AstNativeConstructor(Pair.class), left,
				right);
	}

	@Override
	protected String getInfo() {
		return "Createa  pair";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '*' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

}
