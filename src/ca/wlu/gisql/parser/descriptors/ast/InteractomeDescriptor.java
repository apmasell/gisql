package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstInteractome1;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Syntax for a FLWOR expression. */
public class InteractomeDescriptor extends Parseable<AstNode, Precedence> {
	public final static Parseable<AstNode, Precedence> descriptor = new InteractomeDescriptor();

	private InteractomeDescriptor() {
		super(new TokenReservedWord<AstNode, Precedence>("interactome"),

		new TokenMaybe<AstNode, Precedence>(
				new TokenReservedWord<AstNode, Precedence>("given"),
				new TokenListOf(TokenName.<AstNode, Precedence> get(), ',')),

		TokenMatchCharacter.<AstNode, Precedence> get('{'),

		new TokenReservedWord<AstNode, Precedence>("unknown"),
				TokenMatchCharacter.<AstNode, Precedence> get('='),
				new TokenExpressionFull<AstNode, Precedence>(Precedence
						.expression(), ';'),

				new TokenReservedWord<AstNode, Precedence>("gene"), TokenName
						.<AstNode, Precedence> get(), TokenMatchCharacter
						.<AstNode, Precedence> get('='),
				new TokenExpressionFull<AstNode, Precedence>(Precedence
						.expression(), ';'),

				new TokenReservedWord<AstNode, Precedence>("interaction"),
				TokenName.<AstNode, Precedence> get(), TokenName
						.<AstNode, Precedence> get(), TokenMatchCharacter
						.<AstNode, Precedence> get('='),
				new TokenExpressionFull<AstNode, Precedence>(Precedence
						.expression(), '}'));
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstLiteralList list = (AstLiteralList) params.get(0);
		String[] variables;
		if (list == null) {
			variables = new String[0];
		} else {
			variables = new String[list.size()];
			for (int index = 0; index < list.size(); index++) {
				variables[index] = ((AstName) list.get(index)).getName();
			}
		}

		AstNode membership = params.get(1);
		String genename = ((AstName) params.get(2)).getName();
		AstNode geneexpression = params.get(3);
		String gene1name = ((AstName) params.get(4)).getName();
		String gene2name = ((AstName) params.get(5)).getName();
		AstNode interactionexpression = params.get(6);

		return new AstInteractome1(variables, genename, gene1name, gene2name,
				membership, geneexpression, interactionexpression);
	}

	@Override
	protected String getInfo() {
		return "Custom Interactome";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Closure;
	}
}
