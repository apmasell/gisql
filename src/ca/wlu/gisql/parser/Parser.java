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

	AstNode parseAutoExpression(int level) {
		if (level > environment.getParserKb().maxdepth) {
			return null;
		}

		Stack<AstNode> results = new Stack<AstNode>();

		consumeWhitespace(); /* Do this before testing input length. */
		boolean matched = true;
		while (matched && position < input.length()) {
			matched = false;
			for (Parseable operator : environment.getParserKb().getOperators(
					level)) {
				int oldposition = position;
				int errorposition = error.size();
				if (operator.isPrefixed() == null
						|| operator.isMatchingOperator(input.charAt(position))
						&& (operator.isPrefixed() || results.size() > 0)) {
					if (operator.isPrefixed() != null) {
						position++;
					}
					boolean pop = operator.isPrefixed() != null
							&& !operator.isPrefixed();
					AstNode result = processOperator(operator, (pop ? results
							.peek() : null), level);
					if (result != null) {
						if (pop) {
							results.pop();
						}
						results.push(result);
						matched = true;
						break;
					}
					error.setSize(errorposition);
					position = oldposition;
				}
			}

			if (!matched && level < environment.getParserKb().maxdepth) {
				AstNode result = parseAutoExpression(level + 1);
				if (result != null) {
					results.add(result);
					matched = true;
				}
			}
			consumeWhitespace(); /* Do this before testing input length. */
		}
		if (results.size() == 0) {
			return null;
		}
		if (results.size() == 1) {
			return results.firstElement();
		}
		return new AstApplication(results.toArray(new AstNode[results.size()]));
	}

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
