package ca.wlu.gisql.parser.descriptors.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstEnvironmentLoad;
import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.EnumGenerator;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Allows creation of enumeration types.
 */
public final class EnumerationDescriptor extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> descriptor = new EnumerationDescriptor();

	private EnumerationDescriptor() {
		super(new TokenReservedWord<AstNode, Precedence>("datatype"), TokenName
				.<AstNode, Precedence> get(), TokenMatchCharacter
				.<AstNode, Precedence> get('='), new TokenListOf('|', TokenName
				.<AstNode, Precedence> get()));
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		String name = ((AstName) params.get(0)).getName();
		if (Type.getTypeForName(name) != null) {
			error.push(new ExpressionError(context, "The name " + name
					+ " is already in use for another type.", null));
			return null;
		} else if (runner.getEnvironment().getVariable(name) != null) {
			error.push(new ExpressionError(context, "The name " + name
					+ " is already in use for another value.", null));
			return null;
		}
		List<String> values = new ArrayList<String>();
		for (AstNode value : (AstLiteralList) params.get(1)) {
			String constant = ((AstName) value).getName();
			if (name.equals(constant)) {
				error.push(new ExpressionError(context, "The name " + name
						+ " cannot be used for the type and a value.", null));
				return null;
			} else if (values.contains(constant)) {
				error.push(new ExpressionError(context, "The name " + constant
						+ " cannot be used twice.", null));
				return null;
			} else if (runner.getEnvironment().getVariable(constant) != null) {
				error.push(new ExpressionError(context, "The name " + constant
						+ " is defined to a value of type "
						+ runner.getEnvironment().getTypeOf(constant) + ".",
						null));
				return null;
			}
			values.add(constant);
		}

		return runner.getEnvironment().setVariable(
				name,
				runner.getEnvironment().add(name,
						EnumGenerator.create(name, values)), Type.TypeType) ? new AstEnvironmentLoad(
				name, Type.TypeType)
				: null;

	}

	@Override
	protected String getInfo() {
		return "Assign to variable";
	}

	@Override
	protected char[] getOperators() {
		return null;
	}

	@Override
	public Order getParsingOrder() {
		return Order.Tokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
	}
}