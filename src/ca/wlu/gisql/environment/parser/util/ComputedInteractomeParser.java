package ca.wlu.gisql.environment.parser.util;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.SubExpression;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.fuzzy.TriangularNorm;

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

	abstract protected AstLogic construct(AstNode left, AstNode right,
			TriangularNorm norm);

	protected AstNode construct(Environment environment, AstNode left,
			AstNode right, Stack<String> error) {
		if (!left.isInteractome() || !right.isInteractome()) {
			error.push("Cannot apply to non-interactome operand.");
		}
		return construct(left, right, environment.getTriangularNorm());
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode left = params.get(0);
		AstNode right = params.get(1);
		return construct(environment, left, right, error);
	}

	public char[] getAlternateOperators() {
		return alternateoperators;
	}

	public String getName() {
		return name;
	}

	public int getNestingLevel() {
		return nestinglevel;
	}

	public char getSymbol() {
		return symbol;
	}

	public boolean isMatchingOperator(char c) {
		if (symbol == c)
			return true;
		if (alternateoperators == null)
			return false;
		for (char operator : alternateoperators)
			if (operator == c)
				return true;

		return false;
	}

	public boolean isPrefixed() {
		return false;
	}

	public PrintStream show(PrintStream print) {
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
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(name);
		sb.append(": A ");
		sb.append(symbol);
		sb.append(" B");
		if (alternateoperators != null) {
			for (char c : alternateoperators) {
				sb.append(", A ");
				sb.append(c);
				sb.append(" B");
			}
		}
		return sb;
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new SubExpression(parser) };
	}

}