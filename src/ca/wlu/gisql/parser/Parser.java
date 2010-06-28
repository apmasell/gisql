package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.descriptors.type.TypeNesting;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.runner.LineContext;
import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.Precedence;

/**
 * Parser use input and constructs an abstract syntax tree. The parser is,
 * mostly, an LL(1) recursive descent parser, however, “prefixing” allows it to
 * behave like a pack-rat parser in certain situations involving regular
 * right-hand rules. It is built from {@link Parseable}s which convert tokens
 * into {@link AstNode}s. The parser has a built-in notion of precedence, so
 * each Parseable belongs to a specific precedence level.
 */
public class Parser {

	public static Type parseType(String input) {
		return parseType(new TypeKnowledgeBase(), input);
	}

	public static Type parseType(TypeKnowledgeBase kb, String input) {
		Parser parser = new Parser(null, new LineContext() {

			@Override
			public String getLine() {
				return "0";
			}

			@Override
			public String getSource() {
				return "Internal";
			}
		}, input, null);
		return parser.parseExpression(kb, null, TypeNesting.Arrow);
	}

	private final LineContext context;

	private int currenttokens = 0;

	final Stack<ExpressionError> error = new Stack<ExpressionError>();

	private final String input;

	private final ExpressionRunListener listener;

	private final Stack<Integer> marks = new Stack<Integer>();

	private int position = 0;

	private final ExpressionRunner runner;

	public Parser(ExpressionRunner runner, LineContext context, String input,
			ExpressionRunListener listener) {
		this.runner = runner;
		this.context = context;
		this.input = input;
		this.listener = listener;
	}

	private void appendParserState(StringBuilder sb) {
		if (hasMore()) {
			if (position > 0) {
				sb.append('“');
				sb.append(input.substring(0, position));
				sb.append('”');
			}
			sb.append(" ›").append(peek()).append("‹ ");
			if (position < input.length() - 1) {
				sb.append('“');
				sb.append(input.substring(position + 1));
				sb.append('”');
			}
		} else {
			sb.append('“');
			sb.append(input);
			sb.append('”');
		}
	}

	/** Remove the last mark. */
	void clearMark() {
		marks.pop();
	}

	void consumeWhitespace() {
		while (hasMore() && Character.isWhitespace(peek())) {
			position++;
		}
	}

	int getCurrentTokens() {
		return currenttokens;
	}

	/** Are there more characters available? */
	boolean hasMore() {
		return position < input.length() && peek() != '#';
	}

	public boolean isEmpty() {
		error.clear();
		position = 0;
		consumeWhitespace();
		return !hasMore();
	}

	/** Place a mark at the current */
	void mark() {
		marks.push(position);
	}

	/** Consume a character of input. */
	void next() {
		position++;
	}

	public AstNode parse() {
		error.clear();
		position = 0;
		AstNode result = parseExpression(runner.getEnvironment().getParserKb(),
				null, Precedence.statement());
		if (result == null) {
			if (error.size() == 0) {
				listener.reportErrors(Collections
						.singletonList(new ExpressionError(context,
								"Unrecognised statement.", null)));
			} else {
				listener.reportErrors(error);
			}
			return null;
		} else {
			return result;
		}

	}

	/**
	 * Parse an expression from the current position for a specific precedence
	 * level.
	 */
	<R, P extends Enum<P> & Nextable<P>> R parseAutoExpression(
			ParserKnowledgebase<R, P> knowledgebase, P level) {
		if (level == null) {
			return null;
		}

		R result = null;
		int errorposition = error.size();

		consumeWhitespace(); /* Do this before testing input length. */
		boolean matched = true;
		while (matched && hasMore()) {
			matched = false;
			/* Consider all the parseables in this precedence level... */
			for (Parseable<R, P> operator : knowledgebase.getOperators(level)) {
				/* Attempt to determine if it has a matching operator... */
				int oldposition = position;
				if ((operator.getParsingOrder().needExpression() ? result != null
						: true)
						&& (operator.getParsingOrder().needsCharacters() ? operator
								.isMatchingOperator(peek())
								: true)) {
					if (operator.getParsingOrder().needsCharacters()) {
						next();
					}
					boolean pop = operator.getParsingOrder().needExpression();
					/* Then get the result. */
					R child = processOperator(knowledgebase, operator,
							(pop ? result : null), level);
					/*
					 * If successful, put the results on the stack and parse the
					 * next chunk of input.
					 */
					if (child != null) {
						if (pop || result == null) {
							result = child;
						} else {
							result = knowledgebase.makeApplication(this,
									result, child);
						}
						matched = true;
						break;
					}
					/*
					 * If unsuccessful, reset the parser state and try the next
					 * operator.
					 */
					position = oldposition;
				}
			}

			/*
			 * If we have failed to match any operators at the current level,
			 * recurse...
			 */
			if (!matched) {
				R child = parseAutoExpression(knowledgebase, level.next());
				if (child != null) {
					if (result == null) {
						result = child;
					} else {
						result = knowledgebase.makeApplication(this, result,
								child);
					}
					matched = true;
				}
			}
			consumeWhitespace(); /* Do this before testing input length. */
		}
		if (result != null) {
			error.setSize(errorposition);
		}
		return result;
	}

	/**
	 * Parse a complete expression.
	 * 
	 * @param endofexpression
	 *            The character that indicates a complete expression has been
	 *            found (e.g., ')' when matching a bracketed subexpression). If
	 *            null, the expression must be terminated with the end of input.
	 * @param start
	 *            The lowest precedence level allowed in the expression.
	 *            Probably {@link Precedence#statement()} or
	 *            {@link Precedence#expression()}.
	 */
	<R, P extends Enum<P> & Nextable<P>> R parseExpression(
			ParserKnowledgebase<R, P> knowledgebase, Character endofexpression,
			P start) {
		R e = parseAutoExpression(knowledgebase, start);

		if (e == null) {
			return null;
		}

		int oldposition = position;
		consumeWhitespace();
		boolean eoi = endofexpression == null;
		if (hasMore() ? !eoi && peek() == endofexpression : eoi) {
			position++;
			return e;
		} else if (!hasMore()) {
			pushError("Premature end of input."
					+ (eoi ? "" : " Expected '" + endofexpression + "'."));
			return null;
		} else {
			pushError("Trailing garbage.");

			position = oldposition;
			return null;
		}
	}

	/** Look at the current input character. */
	char peek() {
		return input.charAt(position);
	}

	/**
	 * Attempt to parse one operator by parsing its tokens, then calling
	 * {@link Parseable#construct(ExpressionRunner, List, Stack, ExpressionContext)}
	 * .
	 */
	private <R, P extends Enum<P> & Nextable<P>> R processOperator(
			ParserKnowledgebase<R, P> knowledgebase, Parseable<R, P> operator,
			R left, P level) {
		List<R> params = new ArrayList<R>();
		int oldtokens = currenttokens;
		currenttokens = 0;
		if (left != null) {
			params.add(left);
		}
		for (Token<R, P> task : operator) {
			consumeWhitespace();
			if (!task.parse(knowledgebase, this, level, params)) {
				currenttokens = oldtokens;
				return null;
			}
			currenttokens++;
		}
		currenttokens = oldtokens;
		return operator.construct(runner, params, error, context);
	}

	void pushError(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(message).append('\n');
		appendParserState(sb);
		error.push(new ExpressionError(context.getContextForPosition(position),
				sb.toString(), null));
	}

	/** Consume a character of input and return it. */
	char read() {
		return hasMore() ? input.charAt(position++) : '\0';
	}

	/** Reset the current position to the last mark. */
	void rewindToMark() {
		position = marks.pop();
	}

	/**
	 * Remove the last mark and return the string from that mark to the current
	 * position.
	 */
	String stringFromMark() {
		return input.substring(marks.pop(), position);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Parser: ");
		appendParserState(sb);
		sb.append("\nErrors: ").append(error.size());
		return sb.toString();
	}
}
