package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLambda1;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * This operator parses anonymous functions (aka lambdas). The syntax ('var
 * expression) was chosen because \ is used for set division, so Haskell's (\var
 * -> expression), was not possible and LISP/Scheme (lambda var expression) is
 * bulky.
 */
public class LambdaDescriptor extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new LambdaDescriptor();

	private LambdaDescriptor() {
		super(TokenName.<AstNode, Precedence> get(), TokenExpressionRight
				.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstName name = (AstName) params.get(0);
		AstNode expression = params.get(1);
		return new AstLambda1(name.getName(), expression);
	}

	@Override
	protected String getInfo() {
		return "Anonymous function";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '\'' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}
}
