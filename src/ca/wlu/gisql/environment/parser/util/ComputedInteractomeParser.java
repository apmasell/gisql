package ca.wlu.gisql.environment.parser.util;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.SubExpression;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public abstract class ComputedInteractomeParser implements Parseable {

	private final char[] alternateoperators;

	private final String name;

	private final int nestinglevel;

	private final char symbol;

	public ComputedInteractomeParser(int nestinglevel, char symbol,
			char[] alternateoperators, String name) {
		super();
		this.nestinglevel = nestinglevel;
		this.symbol = symbol;
		this.alternateoperators = alternateoperators;
		this.name = name;
	}

	abstract protected AstNode construct(AstNode left, AstNode right);

	protected final AstNode construct(Environment environment, AstNode left,
			AstNode right, Stack<String> error) {
		if (!left.isInteractome() || !right.isInteractome()) {
			error.push("Cannot apply to non-interactome operand.");
		}
		return construct(left, right);
	}

	public final AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode left = params.get(0);
		AstNode right = params.get(1);
		return construct(environment, left, right, error);
	}

	public final char[] getAlternateOperators() {
		return alternateoperators;
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
		if (symbol == c)
			return true;
		if (alternateoperators == null)
			return false;
		for (char operator : alternateoperators)
			if (operator == c)
				return true;

		return false;
	}

	public final boolean isPrefixed() {
		return false;
	}

	public final void show(ShowablePrintWriter print) {
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

	public final Token[] tasks(Parser parser) {
		return new Token[] { new SubExpression(parser) };
	}

}
