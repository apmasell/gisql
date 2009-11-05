package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Parsers the convenience syntax for functions. A function (f x y), may be
 * written has (x:f y). This is convenient for interactome expressions.
 */
public class ColonOrderDescriptor implements Parseable {
	public static final Parseable descriptor = new ColonOrderDescriptor();

	private static final Token[] tokens = new Token[] { TokenName.self };

	private ColonOrderDescriptor() {
		super();
	}

	@Override
	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode operand = params.get(0);
		AstNode operator = params.get(1);
		return new AstApplication(operator, operand);
	}

	@Override
	public int getPrecedence() {
		return Parser.PREC_LITERAL;
	}

	@Override
	public boolean isMatchingOperator(char c) {
		return c == ':';
	}

	@Override
	public Boolean isPrefixed() {
		return false;
	}

	@Override
	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Function in postfix: expression :function\n");
		print.print("\tEquivalent to: function expresssion");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
