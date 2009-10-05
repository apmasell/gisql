package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Unit;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.LineContext;

public class Parser {

	private static final Token[] literals = new Token[] { TokenName.self,
			TokenReal.self, TokenNumber.self, TokenQuotedString.self };

	public static final int PREC_APPLICATION = 8;

	public static final int PREC_ASSIGN = 1;

	public static final int PREC_CONJUNCT = 4;

	public static final int PREC_DIFF = 2;

	public static final int PREC_DISJUNCT = 3;

	public static final int PREC_FUNCTION = 0;

	public static final int PREC_LIST = 7;

	public static final int PREC_LITERAL = 9;

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
		if (level >= environment.getParserKb().maxdepth) {
			return parseLiterals();
		}

		AstNode left = null;
		if (position >= input.length()) {
			return null;
		}

		int errorposition = error.size();
		for (Parseable operator : environment.getParserKb().getPrefix(level)) {
			int originalposition = position;
			if (operator.isMatchingOperator(input.charAt(position))) {
				position++;
				left = processOperator(operator, null, level);
				if (left != null) {
					error.setSize(errorposition);
					break;
				}
				position = originalposition;
			}
		}

		if (left == null) {
			left = parseAutoExpression(level + 1);
		}

		if (left == null) {
			return null;
		}

		consumeWhitespace(); /* Do this before testing input length. */
		while (position < input.length()) {
			boolean matched = false;
			for (Parseable operator : environment.getParserKb().getOtherfix(
					level)) {
				int oldposition = position;
				errorposition = error.size();
				if (operator.isMatchingOperator(input.charAt(position))) {
					position++;
					AstNode result = processOperator(operator, left, level);
					if (result != null) {
						left = result;
						matched = true;
						break;
					}
					error.setSize(errorposition);
					position = oldposition;
				}
			}

			if (!matched) {
				return left;
			}
			consumeWhitespace(); /* Do this before testing input length. */
		}
		return left;
	}

	private AstNode parseExpression(Character endofexpression) {
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

	private boolean parseLiteral(List<AstNode> results) {
		int oldposition = position;
		for (Token token : literals) {
			position = oldposition;
			if (token.parse(this, PREC_LITERAL, results)) {
				return true;
			}
		}
		return false;
	}

	private AstNode parseLiterals() {
		consumeWhitespace();
		Stack<AstNode> results = new Stack<AstNode>();
		int errorposition = error.size();

		while (position < input.length()) {
			char codepoint = input.charAt(position);

			if (codepoint == '(') {
				position++;
				consumeWhitespace();
				if (position < input.length() && input.charAt(position) == ')') {
					position++;
					results.push(Unit.nilAst);
				} else {
					results.push(parseExpression(')'));
				}
			} else if (codepoint == ':') {
				position++;
				consumeWhitespace();
				if (position >= input.length()) {
					return null;
				}
				AstNode last = results.pop();
				if (parseLiteral(results)) {
					results.push(last);
				} else {
					pushError("Expected literal.");
					return null;
				}
			} else {
				if (!parseLiteral(results)) {
					break;
				}
			}
			consumeWhitespace();
		}
		error.setSize(errorposition);
		if (results.size() == 0) {
			return null;
		}
		if (results.size() == 1) {
			return results.firstElement();
		}
		return new AstApplication(results.toArray(new AstNode[results.size()]));
	}

	private AstNode processOperator(Parseable operator, AstNode left, int level) {
		Token[] todo = operator.tasks();
		List<AstNode> params = new ArrayList<AstNode>();

		if (!operator.isPrefixed()) {
			if (left == null) {
				return null;
			}
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
