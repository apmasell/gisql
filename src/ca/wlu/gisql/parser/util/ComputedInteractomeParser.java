package ca.wlu.gisql.parser.util;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstLambda2;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.AstParameter;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenSubExpression;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class ComputedInteractomeParser implements Parseable {

	private static final Token[] tokens = new Token[] { TokenSubExpression.self };

	private final char[] alternateoperators;

	private final String function;
	private final String name;

	private final int nestinglevel;

	private final char symbol;

	public ComputedInteractomeParser(int nestinglevel, char symbol,
			char[] alternateoperators, String name, String function) {
		super();
		this.nestinglevel = nestinglevel;
		this.symbol = symbol;
		this.alternateoperators = alternateoperators;
		this.name = name;
		this.function = function;
	}

	abstract protected AstNode construct(AstNode left, AstNode right);

	public final AstNode construct(UserEnvironment environment,
			List<AstNode> params, Stack<ExpressionError> error,
			ExpressionContext context) {
		AstNode left = params.get(0);
		AstNode right = params.get(1);
		return construct(left, right);
	}

	public final char[] getAlternateOperators() {
		return alternateoperators;
	}

	public AstNode getFunction() {
		AstParameter left = new AstParameter("__left");
		AstParameter right = new AstParameter("__right");
		left.getType().unify(Type.InteractomeType);
		right.getType().unify(Type.InteractomeType);
		return new AstLambda2(left, new AstLambda2(right,
				construct(left, right)));
	}

	public String getFunctionName() {
		return function;
	}

	public final String getName() {
		return name;
	}

	public final int getPrecedence() {
		return nestinglevel;
	}

	public final char getSymbol() {
		return symbol;
	}

	public final boolean isMatchingOperator(char c) {
		if (symbol == c) {
			return true;
		}
		if (alternateoperators == null) {
			return false;
		}
		for (char operator : alternateoperators) {
			if (operator == c) {
				return true;
			}
		}

		return false;
	}

	public final boolean isPrefixed() {
		return false;
	}

	public final void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print(name);
		print.print(": A ");
		print.print(symbol);
		print.print(" B");
		if (alternateoperators != null) {
			for (char c : alternateoperators) {
				print.print(", A ");
				print.print(c);
				print.print(" B");
			}
		}
	}

	public final Token[] tasks() {
		return tokens;
	}

}
