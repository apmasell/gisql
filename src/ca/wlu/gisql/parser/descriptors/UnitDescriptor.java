package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class UnitDescriptor implements Parseable {
	public static final Parseable descriptor = new UnitDescriptor();

	private static final Token[] tokens = new Token[] { TokenMatchCharacter
			.get(')') };

	private UnitDescriptor() {
		super();
	}

	@Override
	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return Unit.nilAst;
	}

	@Override
	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public boolean isMatchingOperator(char c) {
		return c == '(';
	}

	@Override
	public Boolean isPrefixed() {
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Unit: ()");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
