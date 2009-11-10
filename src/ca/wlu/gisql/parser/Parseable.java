package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.descriptors.LiteralTokenDescriptor;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;

/**
 * Interface for a pluggable parser syntax recogniser. The parser will call
 * {@link #isPrefixed()}, and, if not null, it will call
 * {@link #isMatchingOperator(char)} for every appropriate character in the
 * input stream. If this function returns true, it will call {@link #tasks()}
 * and attempt to parse the required token. If the tokens are correctly parsed,
 * it will call
 * {@link #construct(UserEnvironment, List, Stack, ExpressionContext)} with
 * parsed data.
 */
public interface Parseable extends
		Prioritizable<ParserKnowledgebase, Precedence>,
		Show<ParserKnowledgebase> {

	/**
	 * After parsing is successful, this method must return the abstract syntax
	 * represented.
	 * 
	 * @param environment
	 *            The current environment.
	 * @param params
	 *            The parsed elements found based on the {@link #tasks()}.
	 * @param error
	 *            A stack onto which errors may be pushed in the case of
	 *            failure.
	 * @param context
	 *            The current expression context in which to produce an error.
	 * @return The abstract syntax found or null if there is an error.
	 * */
	public abstract AstNode construct(UserEnvironment environment,
			List<AstNode> params, Stack<ExpressionError> error,
			ExpressionContext context);

	/** Determine is the supplied character is valid for this syntax. */
	public abstract boolean isMatchingOperator(char c);

	/**
	 * Determines the position of this operator relative to other elements. If
	 * false, an operator begins an expression (e.g., !x). If true, an operator
	 * is added on to an existing expression (e.g., a + b). If null, the
	 * operator begins an expression, but there is no set of characters that
	 * defines it. This last option is only used by
	 * {@link LiteralTokenDescriptor}.
	 */
	public abstract Boolean isPrefixed();

	/**
	 * Returns the tokens desired in the ordered desired. Each token's parse
	 * results, if any, will be placed in the params list in
	 * {@link #construct(UserEnvironment, List, Stack, ExpressionContext)}. Not
	 * all tokens return results though.
	 */
	public abstract Token[] tasks();
}