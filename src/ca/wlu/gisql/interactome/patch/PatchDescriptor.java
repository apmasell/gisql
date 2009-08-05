/**
 * 
 */
package ca.wlu.gisql.interactome.patch;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Decimal;
import ca.wlu.gisql.environment.parser.Maybe;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.environment.parser.ParserKnowledgebase;
import ca.wlu.gisql.environment.parser.Token;
import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class PatchDescriptor implements Parseable {
	private final Token[] tokens = new Token[] { new Maybe(Decimal.self) };

	public AstNode construct(Environment environment, List<AstNode> params,
			Stack<String> error) {
		AstNode interactome = params.get(0);
		AstDouble membership = (AstDouble) params.get(1);
		if (interactome.isInteractome()) {
			if (membership != null
					&& (membership.getDouble() > 1.0 || membership.getDouble() < 0.0)) {
				return null;
			}
			return new AstPatch(interactome, (membership == null ? null
					: membership.getDouble()));
		} else {
			return null;
		}
	}

	public int getPrecedence() {
		return Parser.PREC_UNARY_MANGLE;
	}

	public boolean isMatchingOperator(char c) {
		return c == '$';
	}

	public boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("Fill-in-the-blanks: A $ [value]");
	}

	public Token[] tasks() {
		return tokens;
	}
}