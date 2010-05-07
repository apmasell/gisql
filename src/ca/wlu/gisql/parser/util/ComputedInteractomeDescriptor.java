package ca.wlu.gisql.parser.util;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLambda2;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionChild;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public abstract class ComputedInteractomeDescriptor extends Parseable {
	private final String function;

	private final String name;

	private final Precedence nestinglevel;

	private final AstNode node;

	private final char[] symbols;

	public ComputedInteractomeDescriptor(Precedence nestinglevel,
			char[] symbols, String name, String function) {
		super(TokenExpressionChild.self);
		this.nestinglevel = nestinglevel;
		this.symbols = symbols.clone();
		this.name = name;
		this.function = function;
		node = makeLogicFunction();
	}

	abstract public AstNode construct(AstNode left, AstNode right);

	@Override
	public final AstNode construct(ExpressionRunner runner,
			List<AstNode> params, Stack<ExpressionError> error,
			ExpressionContext context) {
		AstNode left = params.get(0);
		AstNode right = params.get(1);
		return construct(left, right);
	}

	protected final char[] getAlternateOperators() {
		return symbols;
	}

	public AstNode getFunction() {
		return node;
	}

	public String getFunctionName() {
		return function;
	}

	@Override
	protected final String getInfo() {
		return name;
	}

	public final String getName() {
		return name;
	}

	@Override
	protected final char[] getOperators() {
		return symbols;
	}

	@Override
	public final Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	public final Precedence getPrecedence() {
		return nestinglevel;
	}

	private AstNode makeLogicFunction() {
		AstParameter left = new AstParameter("__left");
		AstParameter right = new AstParameter("__right");
		left.getType().unify(Type.InteractomeType);
		right.getType().unify(Type.InteractomeType);
		return new AstLambda2(left, new AstLambda2(right,
				construct(left, right)));
	}

}
