package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstToVar;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Allows assignment via (expression @ var). This is kind of awkward, but mostly
 * convenient. Assignment in a functional language is always awkward. This is
 * mostly a relic from he early versions of the query language.
 */
public final class ToVarDescriptor implements Parseable {
	public static final Parseable self = new ToVarDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self };

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode value = params.get(0);
		String name = ((AstName) params.get(1)).getName();
		if (value == null) {
			return null;
		} else {
			return new AstToVar(value, name);
		}
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}

	public boolean isMatchingOperator(char c) {
		return c == '@';
	}

	public Boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Assign to variable: A @ varname");
	}

	public Token[] tasks() {
		return tokens;
	}
}