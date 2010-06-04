package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Parsers the convenience syntax for functions. A function (f x y), may be
 * written has (x:f y). This is convenient for interactome expressions.
 */
public class ColonOrderDescriptor extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> descriptor = new ColonOrderDescriptor();

	private ColonOrderDescriptor() {
		super(TokenName.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode operand = params.get(0);
		AstNode operator = params.get(1);
		return new AstApplication(operator, operand);
	}

	@Override
	protected String getInfo() {
		return "Function in postfix";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { ':' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

}
