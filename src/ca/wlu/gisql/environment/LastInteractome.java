package ca.wlu.gisql.environment;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public final class LastInteractome implements Parseable {
	public static Parseable descriptor = new LastInteractome();

	private LastInteractome() {
		super();
	}

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		if (environment instanceof UserEnvironment) {
			UserEnvironment userenvironment = (UserEnvironment) environment;

			if (userenvironment.getLast() == null) {
				error.push("No previous statement.");
				return null;
			} else {
				return userenvironment.getLast();
			}
		} else {
			error.push("Incompatible environment.");
			return null;
		}
	}

	public int getPrecedence() {
		return 6;
	}

	public boolean isMatchingOperator(char c) {
		return c == '.';
	}

	public boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter print) {
		print.print("Last command: .");
	}

	public Token[] tasks(Parser parser) {
		return null;
	}
}