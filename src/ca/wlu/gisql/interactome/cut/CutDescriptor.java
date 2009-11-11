package ca.wlu.gisql.interactome.cut;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenReal;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class CutDescriptor implements Parseable {
	private final Token[] tokens = new Token[] { TokenReal.self,
			TokenMatchCharacter.get('}') };

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new AstApplication(new AstName("cut"), params.get(0), params
				.get(1));
	}

	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

	public boolean isMatchingOperator(char c) {
		return c == '{';
	}

	public Boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Cut-off (Ax|x>c): A {c}");
	}

	public Token[] tasks() {
		return tokens;
	}
}
