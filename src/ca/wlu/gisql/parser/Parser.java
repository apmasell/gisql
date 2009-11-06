package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.LineContext;

/**
 * Parser use input and constructs an abstract syntax tree. The parser is,
 * mostly, an LL(1) recursive descent parser, however, “prefixing” allows it to
 * behave like a pack-rat parser in certain situations involving regular
 * right-hand rules. It is built from {@link Parseable}s which convert tokens
 * into {@link AstNode}s. The parser has a built-in notion of precedence, so
 * each Parseable belongs to a specific precedence level.
 */
public class Parser {

	public static final int PREC_ASSIGN = 1;

	public static final int PREC_CONJUNCT = 4;

	public static final int PREC_DIFF = 2;

	public static final int PREC_DISJUNCT = 3;

	public static final int PREC_FUNCTION = 0;

	public static final int PREC_LITERAL = 7;

	public static final int PREC_UNARY = 6;

	public static final int PREC_UNARY_MANGLE = 5;

	private final LineContext context;

	private final UserEnvironment environment;

	final Stack<ExpressionError> error = new Stack<ExpressionError>();

	final String input;

	private final ExpressionRunListener listener;

	int position = 0;

	public Parser(UserEnvironment environment, LineContext context,
			String input, ExpressionRunListener listener) {
		this.environment = environment;
		this.context = context;
		this.input = input;
		this.listener = listener;
	}

	void consumeWhitespace() {
		while (position < input.length()
				&& Character.isWhitespace(input.charAt(position))) {
			position++;
		}
	}

	public AstNode parse() {
		error.clear();
		position = 0;
		AstNode result = parseExpression(null);
		if (result == null) {
			if (error.size() == 0) {
				listener.reportErrors(Collections
						.singletonList(new ExpressionError(context,
								"Unrecognized statement.", null)));
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
	AstNode parseAutoExpression(int level) {
		if (level > environment.getParserKb().maxdepth) {
			return null;
		}

		AstNode result = null;

		consumeWhitespace(); /* Do this before testing input length. */
		boolean matched = true;
		while (matched && position < input.length()) {
			matched = false;
			/* Consider all the parseables in this precedence level... */
			for (Parseable operator : environment.getParserKb().getOperators(
					level)) {
				/* Attempt to determine if it has a matching operator... */
				int oldposition = position;
				int errorposition = error.size();
				if (operator.isPrefixed() == null
						|| operator.isMatchingOperator(input.charAt(position))
						&& (operator.isPrefixed() || result != null)) {
					if (operator.isPrefixed() != null) {
						position++;
					}
					boolean pop = operator.isPrefixed() != null
							&& !operator.isPrefixed();
					/* Then get the result. */
					AstNode child = processOperator(operator, (pop ? result
							: null), level);
					/*
					 * If successful, put the results on the stack and parse the
					 * next chunk of input.
					 */
					if (child != null) {
						if (pop || result == null) {
							result = child;
						} else {
							result = new AstApplication(result, child);
						}
						matched = true;
						break;
					}
					/*
					 * If unsuccessful, reset the parser state and try the next
					 * operator.
					 */
					error.setSize(errorposition);
					position = oldposition;
				}
			}

			/*
			 * If we have failed to match any operators at the current level,
			 * recurse...
			 */
			if (!matched && level < environment.getParserKb().maxdepth) {
				AstNode child = parseAutoExpression(level + 1);
				if (child != null) {
					if (result == null) {
						result = child;
					} else {
						result = new AstApplication(result, child);
					}
					matched = true;
				}
			}
			consumeWhitespace(); /* Do this before testing input length. */
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
	 */
	AstNode parseExpression(Character endofexpression) {
		AstNode e = parseAutoExpression(0);

		if (e == null) {
			return null;
		}

		int oldposition = position;
		consumeWhitespace();
		boolean eoi = endofexpression == null;
		if (position < input.length() ? !eoi
				&& input.charAt(position) == endofexpression : eoi) {
			position++;
			return e;
		} else if (position == input.length()) {
			pushError("Premature end of input."
					+ (eoi ? "" : " Expected '" + endofexpression + "'."));
			return null;
		} else {
			pushError("Trailing garbage.");

			position = oldposition;
			return null;
		}
	}

	/**
	 * Attempt to parse one operator by parsing its tokens, then calling
	 * {@link Parseable#construct(UserEnvironment, List, Stack, ca.wlu.gisql.runner.ExpressionContext)}
	 * .
	 */
	private AstNode processOperator(Parseable operator, AstNode left, int level) {
		Token[] todo = operator.tasks();
		List<AstNode> params = new ArrayList<AstNode>();

		if (left != null) {
			params.add(left);
		}
		if (todo != null) {
			for (Token task : todo) {
				consumeWhitespace();
				if (!task.parse(this, level, params)) {
					return null;
				}
			}
		}
		return operator.construct(environment, params, error, context);
	}

	void pushError(String message) {
		error.push(new ExpressionError(context.getContextForPosition(position),
				message, null));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Parser: ");
		if (position < input.length()) {
			if (position > 0) {
				sb.append('"');
				sb.append(input.substring(0, position));
				sb.append('"');
			}
			sb.append(" >").append(input.charAt(position)).append("< ");
			if (position < input.length() - 1) {
				sb.append('"');
				sb.append(input.substring(position + 1));
				sb.append('"');
			}
		} else {
			sb.append('"');
			sb.append(input);
			sb.append('"');
		}
		sb.append("\nErrors: ").append(error.size());
		return sb.toString();
	}
}
