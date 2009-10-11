package ca.wlu.gisql.parser.descriptors;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class EmptyList implements Parseable {

	public static final Parseable descriptor = new EmptyList();
	private static final Token[] tokens = new Token[] { TokenMatchCharacter
			.get(']') };

	private EmptyList() {
		super();
	}

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new AstLiteral(new ListType(new TypeVariable()),
				Collections.EMPTY_LIST);
	}

	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	public boolean isMatchingOperator(char c) {
		return c == '[';
	}

	public Boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
	}

	public Token[] tasks() {
		return tokens;
	}

}
