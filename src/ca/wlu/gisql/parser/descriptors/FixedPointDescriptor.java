package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstFixedPoint1;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * This operator parses anonymous recursive functions.
 */
public class FixedPointDescriptor extends Parseable {

	public static final Parseable descriptor = new FixedPointDescriptor();

	private FixedPointDescriptor() {
		super(TokenName.self, TokenExpressionRight.self);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstName name = (AstName) params.get(0);
		AstNode expression = params.get(1);
		return new AstFixedPoint1(name.getName(), expression);
	}

	@Override
	protected String getInfo() {
		return "Recursive function";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '$' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}
}
