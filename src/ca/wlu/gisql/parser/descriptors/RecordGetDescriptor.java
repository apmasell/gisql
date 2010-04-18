package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstRecordGet;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class RecordGetDescriptor extends Parseable {
	public static final Parseable descriptor = new RecordGetDescriptor();
	private static final Token[] tokens = new Token[] { TokenName.self };

	private RecordGetDescriptor() {
		super();
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return new AstRecordGet(params.get(0), ((AstName) params.get(1))
				.getName());
	}

	@Override
	protected String getInfo() {
		return "Get field from record";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '.' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.UnaryPostfix;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
