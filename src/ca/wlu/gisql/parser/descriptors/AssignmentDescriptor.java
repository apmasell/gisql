package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstEnvironmentStore;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Allows assignment via (expression @ var). This is kind of awkward, but mostly
 * convenient. Assignment in a functional language is always awkward. This is
 * mostly a relic from he early versions of the query language.
 */
public final class AssignmentDescriptor implements Parseable {
	public static final Parseable self = new AssignmentDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self,
			TokenMatchCharacter.get('='), TokenExpressionChild.self };

	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		String name = ((AstName) params.get(0)).getName();
		AstNode value = params.get(1);
		if (value == null) {
			return null;
		} else {
			return new AstEnvironmentStore(value, name, runner.getEnvironment()
					.getTypeOf(name));
		}
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}

	public boolean isMatchingOperator(char c) {
		return false;
	}

	public Boolean isPrefixed() {
		return null;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Assign to variable: name = expression");
	}

	public Token[] tasks() {
		return tokens;
	}
}