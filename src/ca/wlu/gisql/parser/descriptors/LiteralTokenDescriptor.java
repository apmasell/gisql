package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Nextable;

public class LiteralTokenDescriptor<R, P extends Enum<P> & Nextable<P>> extends
		Parseable<R, P> {

	private final P position;

	public LiteralTokenDescriptor(Token<R, P> token, P position) {
		super(token);
		this.position = position;
	}

	@Override
	public R construct(ExpressionRunner runner, List<R> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	@Override
	protected String getInfo() {
		return "Value";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	public P getPrecedence() {
		return position;
	}
}
