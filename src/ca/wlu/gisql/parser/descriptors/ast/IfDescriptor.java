package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstIf;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Syntax for an if expression. */
public class IfDescriptor extends Parseable<AstNode, Precedence> {
	public final static Parseable<AstNode, Precedence> descriptor = new IfDescriptor();

	private IfDescriptor() {
		super(new TokenReservedWord<AstNode, Precedence>("if"),
				TokenExpressionRight.<AstNode, Precedence> get(),
				new TokenReservedWord<AstNode, Precedence>("then"),
				TokenExpressionRight.<AstNode, Precedence> get(),
				new TokenReservedWord<AstNode, Precedence>("else"),
				TokenExpressionRight.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new AstIf(params.get(0), params.get(1), params.get(2));
	}

	@Override
	protected String getInfo() {
		return "Conditional evaluation";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}
}
