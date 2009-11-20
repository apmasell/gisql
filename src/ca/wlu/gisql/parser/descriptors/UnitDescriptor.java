package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parses the empty list written as “()”. */
public class UnitDescriptor implements Parseable {
	public static final Parseable descriptor = new UnitDescriptor();

	private static final Logger log = Logger.getLogger(UnitDescriptor.class);

	private static final Token[] tokens = new Token[] { TokenMatchCharacter
			.get(')') };

	private UnitDescriptor() {
		super();
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
	public Precedence getPrecedence() {
		return Precedence.Value;
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
		print.println("Unit: ()");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
