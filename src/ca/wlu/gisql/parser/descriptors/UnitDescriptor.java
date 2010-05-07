package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Parses the empty list written as “()”. */
public class UnitDescriptor extends Parseable {
	public static final Parseable descriptor = new UnitDescriptor();

	private static final Logger log = Logger.getLogger(UnitDescriptor.class);

	private UnitDescriptor() {
		super(TokenMatchCharacter.get(')'));
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		try {
			return new AstLiteralReference(Unit.class.getField("nil"),
					Type.UnitType);
		} catch (SecurityException e) {
			log.error("Failed to get nil field.", e);
		} catch (NoSuchFieldException e) {
			log.error("Failed to get nil field.", e);
		}
		return null;
	}

	@Override
	protected String getInfo() {
		return "Unit";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '(' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}
}
