package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstRecordNew;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.parser.TokenSequence;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class RecordNewDescriptor extends Parseable {
	public static final Parseable descriptor = new RecordNewDescriptor();
	private static final Token[] tokens = new Token[] {
			new TokenMaybe(new TokenExpressionFull(';')),
			new TokenListOf(
					new TokenSequence(TokenName.self, TokenMatchCharacter
							.get('='), new TokenExpressionFull(null)), ';'),
			TokenMatchCharacter.get('>') };

	private RecordNewDescriptor() {
		super();
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstRecordNew node = new AstRecordNew(params.get(0));
		AstLiteralList list = (AstLiteralList) params.get(1);
		for (int index = 0; index < list.size(); index += 2) {
			node
					.add(((AstName) list.get(index)).getName(), list
							.get(index + 1));
		}

		return node;
	}

	@Override
	protected String getInfo() {
		return "Create record";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '<' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
