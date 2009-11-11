package ca.wlu.gisql.interactome.logic;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLogic;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** This is a special syntax parseable to handle complemented/negated sets. */
public class Complement implements Parseable {
	public static final Parseable descriptor = new Complement();

	private static final Token[] tokens = new Token[] { TokenExpressionChild.self };

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode interactome = params.get(0);

		return AstLogic.makeNegation(interactome);
	}

	public Precedence getPrecedence() {
		return Precedence.UnaryPrefix;
	}

	public boolean isMatchingOperator(char c) {
		return c == '!' || c == '¬';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Complement (1-Ax): ¬A, !A");
	}

	public Token[] tasks() {
		return tokens;
	}
}
