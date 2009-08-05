/**
 * 
 */
package ca.wlu.gisql.interactome.tovar;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Name;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class ToVarDescriptor implements Parseable {
	private final Token[] tokens = new Token[] { new Name() };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		String name = ((AstString) params.get(1)).getString();
		if (name == null) {
			error.push("Missing variable name.");
			return null;
		}
		return new AstToVar(environment, interactome, name);
	}

	public int getPrecedence() {
		return Parser.PREC_ASSIGN;
	}

	public boolean isMatchingOperator(char c) {
		return c == '@';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Assign to variable: A @ varname");
	}

	public Token[] tasks() {
		return tokens;
	}
}