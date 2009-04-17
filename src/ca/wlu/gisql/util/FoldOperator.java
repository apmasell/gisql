package ca.wlu.gisql.util;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.Environment;
import ca.wlu.gisql.Parser;
import ca.wlu.gisql.interactome.Interactome;

public class FoldOperator implements Parseable {
	private ParseableBinaryOperation binary;

	public FoldOperator(ParseableBinaryOperation binary) {
		super();
		this.binary = binary;
	}

	@SuppressWarnings("unchecked")
	public Interactome construct(Environment environment, List<Object> params,
			Stack<String> error) {
		List<Interactome> interactomes = (List<Interactome>) params.get(0);
		Interactome left = null;
		for (Interactome interactome : interactomes) {
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
				print.print(" { A, B, C, ... }");
			}
		}
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		sb.append(binary.getName());
		sb.append(" (Folded): ");
		sb.append(binary.getSymbol());
		sb.append(" { A, B, C, ... }");
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

	public Parser.NextTask[] tasks(Parser parser) {
		return new Parser.NextTask[] { parser.new Literal('{'),
				parser.new ListOf(parser.new Expression(), ','),
				parser.new Literal('}') };
	}

}