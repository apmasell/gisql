package ca.wlu.gisql.interactome.coreicity;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Number;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.environment.parser.ast.AstInteger;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class DeltaCoreicityDescriptor implements Parseable {

	private static final Token[] tokens = new Token[] { new Word("core"),
			Number.self };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		long delta = ((AstInteger) params.get(1)).getInt();
		if (interactome.isInteractome()) {
			return new AstDeltaCoreicity(interactome, (int) delta);
		} else {
			return null;
		}
	}

	public int getPrecedence() {
		return Parser.PREC_UNARY_MANGLE;
	}

	public boolean isMatchingOperator(char c) {
		return c == ':';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		// TODO Auto-generated method stub

	}

	public Token[] tasks() {
		return tokens;
	}

}
