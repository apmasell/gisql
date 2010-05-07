package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** This operator extracts the help information from a function and returns it. */
public class HelpDescriptor extends Parseable {

	public static final Parseable descriptor = new HelpDescriptor();

	public static void helpFor(StringBuilder sb, String name, Type type,
			Object value) {
		sb.append(name).append(" :: ").append(type);
		if (value == null) {
			return;
		}
		String representation;
		if (value instanceof GenericFunction) {
			representation = ((GenericFunction) value).getDescription();
		} else if (value instanceof AstNative) {
			representation = ((AstNative) value).getDescription();
		} else {
			representation = value.toString();
		}
		sb.append("\n\t").append(representation);
	}

	private HelpDescriptor() {
		super(TokenName.self);
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		String name = ((AstName) params.get(0)).getName();
		StringBuilder result = new StringBuilder();

		if (BuiltInResolver.get(name) != null) {
			AstNative node = BuiltInResolver.get(name);
			helpFor(result, name, node.getType(), node);

		} else {
			Type type = runner.getEnvironment().getTypeOf(name);

			if (type == null) {
				result.append("unknown name");
			} else {
				helpFor(result, name, type, runner.getEnvironment()
						.getVariable(name));
			}
		}
		return new AstLiteral(Type.StringType, result.toString());
	}

	@Override
	protected String getInfo() {
		return "Help";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '?' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Closure;
	}

}
