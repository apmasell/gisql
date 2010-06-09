package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstTypeCheck;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenType;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Parsers the convenience syntax for functions. A function (f x y), may be
 * written has (x:f y). This is convenient for interactome expressions.
 */
public class TypeCheckDescriptor extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> descriptor = new TypeCheckDescriptor();

	private TypeCheckDescriptor() {
		super(new TokenMatchCharacter<AstNode, Precedence>("::"),
				TokenType.self);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode expression = params.get(0);
		Type type = (Type) ((AstLiteral) params.get(1)).getValue();
		return new AstTypeCheck(expression, type);
	}

	@Override
	protected String getInfo() {
		return "Type check";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Channel;
	}

}
