package ca.wlu.gisql.environment.parser.util;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ListExpression;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class FoldOperator implements Parseable {
	private final ComputedInteractomeParser binary;

	public FoldOperator(ComputedInteractomeParser binary) {
		super();
		this.binary = binary;
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstList interactomes = (AstList) params.get(0);
		AstNode left = null;
		for (AstNode interactome : interactomes) {
			if (!interactome.isInteractome()) {
				error.push("List contains non-interactome objects.");
				return null;
			}
			if (left == null) {
				left = interactome;
			} else {
				left = binary.construct(environment, left, interactome, error);
				if (left == null)
					return null;
			}
		}
		return left;
	}

	public int getNestingLevel() {
		return binary.getNestingLevel();
	}

	public boolean isMatchingOperator(char c) {
		return binary.isMatchingOperator(c);
	}

	public boolean isPrefixed() {
		return true;
	}

	public PrintStream show(PrintStream print) {
		print.print(binary.getName());
		print.print(" (Folded): ");
		print.print(binary.getSymbol());
		print.print(" { A, B, C, ... }");
		char[] alternateoperators = binary.getAlternateOperators();
		if (alternateoperators != null) {
			for (char c : alternateoperators) {
				print.print(", ");
				print.print(c);
				print.print(" list");
			}
		}
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(binary.getName());
		sb.append(" (Folded): ");
		sb.append(binary.getSymbol());
		sb.append(" list");
		char[] alternateoperators = binary.getAlternateOperators();
		if (alternateoperators != null) {
			for (char c : alternateoperators) {
				sb.append(", ");
				sb.append(c);
				sb.append(" { A, B, C, ... }");
			}
		}
		return sb;
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new ListExpression(parser) };
	}

}
