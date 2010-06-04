package ca.wlu.gisql.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import name.masella.iterator.ArrayIterator;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.Prioritizable;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Class for a pluggable parser syntax recogniser. The parser will call
 * {@link #getParsingOrder()}, and, if appropriate, it will call
 * {@link #getOperators()} for every appropriate character in the input stream.
 * If this function returns true, it will call {@link #tasks()} and attempt to
 * parse the required token. If the tokens are correctly parsed, it will call
 * {@link #construct(ExpressionRunner, List, Stack, ExpressionContext)} with
 * parsed data.
 */
public abstract class Parseable<R, P extends Enum<P> & Nextable<P>> implements
		Iterable<Token<R, P>>, Prioritizable<ParserKnowledgebase<R, P>, P>,
		Show<ParserKnowledgebase<R, P>> {

	public enum Order {
		CharacterTokens, ExpressionCharacterTokens, Tokens
	};

	private final Token<R, P>[] tokens;

	protected Parseable(Token<R, P>... tokens) {
		super();
		if (tokens.length == 0) {
			throw new IllegalArgumentException();
		}
		this.tokens = tokens;
	}

	/**
	 * After parsing is successful, this method must return the abstract syntax
	 * represented.
	 * 
	 * @param runner
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
	public abstract R construct(ExpressionRunner runner, List<R> params,
			Stack<ExpressionError> error, ExpressionContext context);

	protected abstract String getInfo();

	/** Determine is the supplied character is valid for this syntax. */
	protected abstract char[] getOperators();

	/**
	 * Determines the position of this operator relative to other elements.
	 */
	public abstract Order getParsingOrder();

	public final boolean isMatchingOperator(char needle) {
		for (char haystack : getOperators()) {
			if (needle == haystack) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the tokens desired in the ordered desired. Each token's parse
	 * results, if any, will be placed in the params list in
	 * {@link #construct(ExpressionRunner, List, Stack, ExpressionContext)}. Not
	 * all tokens return results though.
	 */
	public final Iterator<Token<R, P>> iterator() {
		return new ArrayIterator<Token<R, P>>(tokens);
	}

	@Override
	public final void show(ShowablePrintWriter<ParserKnowledgebase<R, P>> print) {
		print.print(getInfo());
		print.print(':');

		if (getParsingOrder() == Order.ExpressionCharacterTokens) {
			print.print(" <exp>");
		}
		if (getParsingOrder() != Order.Tokens) {
			print.print(" [");
			for (char c : getOperators()) {
				print.print(c);
			}
			print.print(']');
		}

		for (Token<R, P> token : this) {
			print.print(' ');
			print.print(token);
		}
		print.println();
	}
}