package ca.wlu.gisql.interactome.tovar;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstToVar;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class ToVarDescriptor implements Parseable {
	private static final Token[] tokens = new Token[] { TokenName.self };

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode interactome = params.get(0);
		String name = ((AstName) params.get(1)).getName();
		return new AstToVar(interactome, name);
	}

	public int getPrecedence() {
		return Parser.PREC_ASSIGN;
	}

	public boolean isMatchingOperator(char c) {
		return c == '@';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Assign to variable: A @ varname");
	}

	public Token[] tasks() {
		return tokens;
	}
}