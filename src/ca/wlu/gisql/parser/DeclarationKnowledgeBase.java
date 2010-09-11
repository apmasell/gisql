package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.util.PairDeclaration;
import ca.wlu.gisql.ast.util.ParameterDeclaration;
import ca.wlu.gisql.ast.util.TerminalDeclaration;
import ca.wlu.gisql.parser.descriptors.BracketedExpressionDescriptor;
import ca.wlu.gisql.parser.descriptors.DeclarationNesting;
import ca.wlu.gisql.parser.descriptors.LiteralTokenDescriptor;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;

public class DeclarationKnowledgeBase extends
		ParserKnowledgebase<ParameterDeclaration, DeclarationNesting> {

	private static class PairDescriptor extends
			Parseable<ParameterDeclaration, DeclarationNesting> {
		private PairDescriptor() {
			super(TokenExpressionRight
					.<ParameterDeclaration, DeclarationNesting> get());
		}

		@Override
		public ParameterDeclaration construct(ExpressionRunner runner,
				List<ParameterDeclaration> params,
				Stack<ExpressionError> error, ExpressionContext context) {
			if (params.get(0) == null || params.get(1) == null) {
				return null;
			}
			return new PairDeclaration(params.get(0), params.get(1));
		}

		@Override
		protected String getInfo() {
			return "A pair of values";
		}

		@Override
		protected char[] getOperators() {
			return new char[] { '*' };
		}

		@Override
		public Order getParsingOrder() {
			return Order.ExpressionCharacterTokens;
		}

		@Override
		public DeclarationNesting getPrecedence() {
			return DeclarationNesting.Pair;
		}
	}

	private static final Parseable<ParameterDeclaration, DeclarationNesting> pairdescriptor = new PairDescriptor();

	public DeclarationKnowledgeBase() {
		super(DeclarationNesting.values(),
				"Declaration of parameters in lambda expressions.");
		installOperator(BracketedExpressionDescriptor.declarationdescriptor);
		installOperator(pairdescriptor);
		installOperator(new LiteralTokenDescriptor<ParameterDeclaration, DeclarationNesting>(
				TokenName.<ParameterDeclaration, DeclarationNesting> get(),
				DeclarationNesting.Value));
	}

	@Override
	ParameterDeclaration makeApplication(Parser parser,
			ParameterDeclaration left, ParameterDeclaration right) {
		return null;
	}

	@Override
	ParameterDeclaration makeBoolean(boolean b) {
		return null;
	}

	@Override
	ParameterDeclaration makeName(Parser parser, String name) {
		return new TerminalDeclaration(name, null);
	}

}
