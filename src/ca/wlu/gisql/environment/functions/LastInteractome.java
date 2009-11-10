package ca.wlu.gisql.environment.functions;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public final class LastInteractome implements Parseable {
	public static final Parseable descriptor = new LastInteractome();

	private LastInteractome() {
		super();
	}

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		if (environment.getLast() == null) {
			error.push(new ExpressionError(context, "No previous statement.",
					null));
			return null;
		} else {
			return environment.getLast();
		}
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	public boolean isMatchingOperator(char c) {
		return c == '.';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Last command: .");
	}

	public Token[] tasks() {
		return null;
	}
}