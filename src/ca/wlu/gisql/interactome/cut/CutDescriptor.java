package ca.wlu.gisql.interactome.cut;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class CutDescriptor implements Parseable {
	private final Token[] tokens = new Token[] { Decimal.self, Literal.get(']') };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		double cutoff = ((AstDouble) params.get(1)).getDouble();
		if (cutoff > 1.0 || cutoff < 0 || !interactome.isInteractome())
			return null;
		return new AstCut(interactome, cutoff);
	}

	public int getPrecedence() {
		return Parser.PREC_UNARY_MANGLE;
	}

	public boolean isMatchingOperator(char c) {
		return c == '[';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Cut-off (Ax|x>c): A [c]");
	}

	public Token[] tasks() {
		return tokens;
	}
}