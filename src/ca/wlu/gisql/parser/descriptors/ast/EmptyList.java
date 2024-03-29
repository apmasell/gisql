package ca.wlu.gisql.parser.descriptors.ast;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.TypeVariable;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Parses the empty list written as “[]”. This is handled separately from
 * {@link LiteralList} since {@link TokenListOf} must match at least one item.
 */
public class EmptyList extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new EmptyList();
	private static final Logger log = Logger.getLogger(EmptyList.class);

	private EmptyList() {
		super(TokenMatchCharacter.<AstNode, Precedence> get(']'));
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		try {
			return new AstLiteralReference(Collections.class
					.getField("EMPTY_LIST"), new ListType(new TypeVariable()));
		} catch (SecurityException e) {
			log.error("Failed to access field.", e);
			return null;
		} catch (NoSuchFieldException e) {
			log.error("Failed to access field.", e);
			return null;
		}
	}

	@Override
	protected String getInfo() {
		return "Empty List";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '[' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

}
