package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstTypeOf;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * This allows the user to introspect the inferred type of an operation. It is
 * probably not useful to most users, but it is to developers.
 */
public class TypeOfDescriptor extends Parseable {

	public static final Parseable descriptor = new TypeOfDescriptor();

	private static final Token[] tokens = new Token[] { TokenExpressionRight.self };

	private TypeOfDescriptor() {
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode expression = params.get(0);
		if (expression == null) {
			return null;
		} else {
			return new AstTypeOf(expression);
		}
	}

	@Override
	protected String getInfo() {
		return "Type of";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '#' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
