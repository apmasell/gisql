package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstEnvironmentStore;
import ca.wlu.gisql.ast.AstFixedPoint1;
import ca.wlu.gisql.ast.AstLambda1;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstTypeCheck;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.parser.TokenType;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Allows assignment of functions with automatic recursion via (fun x y z =
 * expression).
 */
public final class RecursiveFunctionDescriptor extends
		Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> self = new RecursiveFunctionDescriptor();

	private RecursiveFunctionDescriptor() {
		super(TokenName.<AstNode, Precedence> get(), new TokenListOf(TokenName
				.<AstNode, Precedence> get(), null),
				new TokenMaybe<AstNode, Precedence>(
						new TokenMatchCharacter<AstNode, Precedence>("::"),
						TokenType.self), TokenMatchCharacter
						.<AstNode, Precedence> get('='), TokenExpressionChild
						.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		String name = ((AstName) params.get(0)).getName();
		AstNode expression = params.get(3);

		AstLiteralList arguments = (AstLiteralList) params.get(1);
		Type type = params.get(2) == null ? null : (Type) ((AstLiteral) params
				.get(2)).getValue();
		Type[] argumenttypes;
		if (type == null) {
			argumenttypes = new Type[arguments.size()];
			for (int index = 0; index < argumenttypes.length; index++) {
				argumenttypes[index] = new TypeVariable();
			}
		} else {
			argumenttypes = type.getParameters();
			if (argumenttypes.length < arguments.size()) {
				error.push(new ExpressionError(context, "There are "
						+ arguments.size() + " but the type " + type
						+ " has only " + type.getArrowDepth() + " arguments.",
						null));
				return null;
			}
		}

		for (int index = arguments.size() - 1; index >= 0; index--) {
			expression = new AstLambda1(((AstName) arguments.get(index))
					.getName(), expression, argumenttypes[index]);
		}

		if (type != null) {
			expression = new AstTypeCheck(expression, type);
		}

		return new AstEnvironmentStore(new AstFixedPoint1(name, expression),
				name, runner.getEnvironment().getTypeOf(name));
	}

	@Override
	protected String getInfo() {
		return "Define a function";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}
}