package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstRecordNew;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
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
import ca.wlu.gisql.util.ShowablePrintWriter;

public class RecordNewDescriptor implements Parseable {
	public static final Parseable descriptor = new RecordNewDescriptor();
	private static final Token[] tokens = new Token[] {
			new TokenMaybe(new TokenSequence(new TokenExpressionFull(null),
					TokenMatchCharacter.get(';'))),
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
		AstRecordNew node = new AstRecordNew(params.get(0) == null ? null
				: ((AstLiteralList) params.get(0)).get(0));

		for (AstNode parameter : (AstLiteralList) params.get(1)) {
			AstLiteralList list = (AstLiteralList) parameter;
			node.add(((AstName) list.get(0)).getName(), list.get(1));
		}

		return node;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

	@Override
	public boolean isMatchingOperator(char c) {
		return c == '<';
	}

	@Override
	public Boolean isPrefixed() {
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print
				.println("Create record: < field1 = value1 ; field2 = value2 ; field3 = value3 > OR < existing; field1 = value1 ; field2 = value2 >");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
