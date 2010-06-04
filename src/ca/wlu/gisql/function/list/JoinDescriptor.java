package ca.wlu.gisql.function.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class JoinDescriptor extends Parseable<AstNode, Precedence> {

	JoinDescriptor() {
		super(TokenExpressionChild.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode head = params.get(0);
		AstNode tail = params.get(1);
		if (head == null || tail == null) {
			return null;
		} else {
			return new AstApplication(new Join(runner), head, tail);
		}
	}

	@Override
	protected String getInfo() {
		return "Join two lists";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '+' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Junction;
	}

}
