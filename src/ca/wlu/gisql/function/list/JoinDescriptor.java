package ca.wlu.gisql.function.list;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
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

public class JoinDescriptor implements Parseable {

	private static final Token[] tokens = new Token[] { TokenExpressionChild.self };

	JoinDescriptor() {
		super();
	}

	@Override
	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode head = params.get(0);
		AstNode tail = params.get(1);
		if (head == null || tail == null) {
			return null;
		} else {
			return new AstApplication(Join.function, head, tail);
		}
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Junction;
	}

	@Override
	public boolean isMatchingOperator(char c) {
		return c == ';';
	}

	@Override
	public Boolean isPrefixed() {
		return false;
	}

	@Override
	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Join two lists: list1; list2");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
