package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstEnvironmentStore;
import ca.wlu.gisql.ast.AstFixedPoint1;
import ca.wlu.gisql.ast.AstLambda1;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Allows assignment of functions with automatic recursion via (fun x y z =
 * expression).
 */
public final class RecursiveFunctionDescriptor extends Parseable {
	public static final Parseable self = new RecursiveFunctionDescriptor();

	private RecursiveFunctionDescriptor() {
		super(TokenName.self, new TokenListOf(TokenName.self, null),
				TokenMatchCharacter.get('='), TokenExpressionChild.self);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		String name = ((AstName) params.get(0)).getName();
		AstNode expression = params.get(2);
		AstLiteralList arguments =((AstLiteralList) params.get(1));
		for (int index = arguments.size()-1;index>=0;index--) {
			expression = new AstLambda1(((AstName) arguments.get(index)).getName(), expression);
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