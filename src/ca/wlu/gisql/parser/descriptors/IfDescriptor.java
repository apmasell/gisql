package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstIf;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Syntax for an if expression. */
public class IfDescriptor extends Parseable {
	public final static Parseable descriptor = new IfDescriptor();

	private IfDescriptor() {
		super(new TokenReservedWord("if"), TokenExpressionChild.self,
				new TokenReservedWord("then"), TokenExpressionChild.self,
				new TokenReservedWord("else"), TokenExpressionChild.self);
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
