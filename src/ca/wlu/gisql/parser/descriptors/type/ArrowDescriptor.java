package ca.wlu.gisql.parser.descriptors.type;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;

public class ArrowDescriptor extends Parseable<Type, TypeNesting> {

	public static Parseable<Type, TypeNesting> self = new ArrowDescriptor();

	private ArrowDescriptor() {
		super(TokenExpressionRight.<Type, TypeNesting> get());
	}

	@Override
	public Type construct(ExpressionRunner runner, List<Type> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new ArrowType(params.get(0), params.get(1));
	}

	@Override
	protected String getInfo() {
		return "Function Type";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '→', '>' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	@Override
	public TypeNesting getPrecedence() {
		return TypeNesting.Arrow;
	}

}
