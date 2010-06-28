package ca.wlu.gisql.parser.descriptors.type;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;

public class MaybeDescriptor extends Parseable<Type, TypeNesting> {

	public static Parseable<Type, TypeNesting> self = new MaybeDescriptor();

	private MaybeDescriptor() {
		super(TokenExpressionChild.<Type, TypeNesting> get(),
				TokenMatchCharacter.<Type, TypeNesting> get('?'));
	}

	@Override
	public Type construct(ExpressionRunner runner, List<Type> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new MaybeType(params.get(0));
	}

	@Override
	protected String getInfo() {
		return "List of";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '¿' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	@Override
	public TypeNesting getPrecedence() {
		return TypeNesting.Receptacle;
	}

}
