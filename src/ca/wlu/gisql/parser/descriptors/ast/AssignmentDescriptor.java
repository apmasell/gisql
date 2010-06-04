package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstEnvironmentStore;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.parser.TokenName;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/**
 * Allows assignment via (var = expression).
 */
public final class AssignmentDescriptor extends Parseable<AstNode, Precedence> {
	public static final Parseable<AstNode, Precedence> self = new AssignmentDescriptor();

	private AssignmentDescriptor() {
		super(TokenName.<AstNode, Precedence> get(), TokenMatchCharacter
				.<AstNode, Precedence> get('='), TokenExpressionChild
				.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		String name = ((AstName) params.get(0)).getName();
		AstNode value = params.get(1);
		if (value == null) {
			return null;
		} else {
			return new AstEnvironmentStore(value, name, runner.getEnvironment()
					.getTypeOf(name));
		}
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