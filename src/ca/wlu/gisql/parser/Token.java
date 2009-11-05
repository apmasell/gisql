package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

/**
 * Super class for all tokens. Each token represents one atomic unit of input,
 * which may be separated by arbitrary white space. Tokens have access to
 * manipulate the {@link Parser}'s current state.
 */
public abstract class Token {
	/**
	 * Attempt to parse a token.
	 * 
	 * @param parser
	 *            The parser whose state should be modified.
	 * @param level
	 *            The precedence depth in the parse tree.
	 * @param results
	 *            If there is a result, it should be appended to this list.
	 * @return True is parsing was successful.
	 */
	abstract boolean parse(Parser parser, int level, List<AstNode> results);
}