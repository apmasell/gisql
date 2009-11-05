package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLambda1;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * This operator parses anonymous functions (aka lambdas). The syntax ('var
 * expression) was chosen because \ is used for set division, so Haskell's (\var
 * -> expression), was not possible and LISP/Scheme (lambda var expression) is
 * bulky.
 */
public class LambdaDescriptor implements Parseable {

	public static final Parseable descriptor = new LambdaDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self,
			TokenExpressionRight.self };

	private LambdaDescriptor() {
	}

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstName name = (AstName) params.get(0);
		AstNode expression = params.get(1);
		return new AstLambda1(name.getName(), expression);
	}

	public int getPrecedence() {
		return Parser.PREC_FUNCTION;
	}

	public boolean isMatchingOperator(char c) {
		return c == '\'';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Anonymous function: 'variable expression");
	}

	public Token[] tasks() {
		return tokens;
	}

}
