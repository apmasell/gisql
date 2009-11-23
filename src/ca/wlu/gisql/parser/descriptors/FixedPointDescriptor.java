package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstFixedPoint1;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * This operator parses anonymous recursive functions.
 */
public class FixedPointDescriptor implements Parseable {

	public static final Parseable descriptor = new FixedPointDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self,
			TokenExpressionRight.self };

	private FixedPointDescriptor() {
	}

	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstName name = (AstName) params.get(0);
		AstNode expression = params.get(1);
		return new AstFixedPoint1(name.getName(), expression);
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	public boolean isMatchingOperator(char c) {
		return c == '$';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Recursive function: $self expression");
	}

	public Token[] tasks() {
		return tokens;
	}

}