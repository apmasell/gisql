package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.ParameterDeclaration;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.Precedence;

/** Parses a bracketed subexpression. */
public class BracketedExpressionDescriptor<R, P extends Enum<P> & Nextable<P>>
		extends Parseable<R, P> {
	public static final Parseable<ParameterDeclaration, DeclarationNesting> declarationdescriptor = new BracketedExpressionDescriptor<ParameterDeclaration, DeclarationNesting>(
			DeclarationNesting.values());

	public static final Parseable<AstNode, Precedence> descriptor = new BracketedExpressionDescriptor<AstNode, Precedence>(
			Precedence.values());

	public static final Parseable<Type, TypeNesting> typedescriptor = new BracketedExpressionDescriptor<Type, TypeNesting>(
			TypeNesting.values());

	private final P[] values;

	private BracketedExpressionDescriptor(P[] values) {
		super(new TokenExpressionFull<R, P>(values[0], ')'));
		this.values = values;
	}

	@Override
	public R construct(ExpressionRunner runner, List<R> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	@Override
	protected String getInfo() {
		return "Control Order of Operations";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '(' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	@Override
	public P getPrecedence() {
		return values[values.length - 1];
	}

}
