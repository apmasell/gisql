package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstTypeOf;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenReservedWord;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * This allows the user to introspect the inferred type of an operation. It is
 * probably not useful to most users, but it is to developers.
 */
public class TypeOfDescriptor extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new TypeOfDescriptor();

	private TypeOfDescriptor() {
		super(new TokenReservedWord<AstNode, Precedence>("typeof"),
				TokenExpressionRight.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		AstNode expression = params.get(0);
		if (expression == null) {
			return null;
		} else {
			return new AstTypeOf(expression);
		}
	}

	@Override
	protected String getInfo() {
		return "Type of";
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
		return Precedence.Closure;
	}

}
