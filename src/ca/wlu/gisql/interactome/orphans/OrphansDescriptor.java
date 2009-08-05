/**
 * 
 */
package ca.wlu.gisql.interactome.orphans;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.Word;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.interactome.delay.AstDelay;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class OrphansDescriptor implements Parseable {
	private final Token[] tokens = new Token[] { new Word("orphans") };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		return new AstOrphans(new AstDelay(params.get(0)));
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
		print.print("Find disconnected nodes: A : orphans");
	}

	public Token[] tasks() {
		return tokens;
	}
}