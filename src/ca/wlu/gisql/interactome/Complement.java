package ca.wlu.gisql.interactome;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.SubExpression;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstLogic;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class Complement implements Parseable {
	public final static Parseable descriptor = new Complement();

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		if (interactome.isInteractome())
			return AstLogic.makeNegation(interactome, environment
					.getTriangularNorm());
		else
			return null;
	}

	public int getPrecedence() {
		return 5;
	}

	public boolean isMatchingOperator(char c) {
		return c == '!' || c == '¬';
	}

	public boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter print) {
		print.print("Complement (1-Ax): ¬A, !A");
	}

	public Token[] tasks(Parser parser) {
		return new Token[] { new SubExpression(parser) };
	}
}
