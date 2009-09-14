package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstVoid;
import ca.wlu.gisql.interactome.Interactome;

public class Parser {
	public enum Result {
		Executable, Failure, Interactome
	};

	public static final int PREC_ASSIGN = 1;

	public final static int PREC_CONJUNCT = 4;

	public static final int PREC_DIFF = 2;

	public final static int PREC_DISJUNCT = 3;

	public static final int PREC_FUNCTION = 0;

	public static final int PREC_LITERAL = 7;

	public static final int PREC_UNARY = 6;

	public static final int PREC_UNARY_MANGLE = 5;

	Environment environment;

	final Stack<String> error = new Stack<String>();

	final String input;

	int position = 0;
	private AstNode result = null;

	private Result state = null;

	public Parser(Environment environment, String input) {
		this.environment = environment;
		this.input = input;
		reparse();
	}

	void consumeWhitespace() {
		while (position < input.length()
				&& Character.isWhitespace(input.charAt(position))) {
			position++;
		}
	}

	public void execute() {
		if (state == Result.Executable)
			((AstVoid) result).execute();
	}

	public Interactome get() {
		if (state == Result.Interactome)
			return result.asInteractome();
		else
			return null;
	}

	public String getErrors() {
		StringBuilder sb = new StringBuilder();
		if (error.size() == 0) {
			sb.append("Unrecognized statement.");
		} else {
			while (error.size() > 0) {
				sb.append(error.pop());
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	public Result getParseResult() {
		return state;
	}

	public AstNode getRaw() {
		if (state == null || state == Result.Failure)
			return null;
		else
			return result;
	}

	AstNode parseAutoExpression(int level) {
		AstNode left = null;
		if (position >= input.length())
			return null;

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

		if (left == null)
			left = (level >= environment.getParserKb().maxdepth ? parseIdentifier()
					: parseAutoExpression(level + 1));

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
		if (position < input.length() ? (!eoi)
				&& input.charAt(position) == endofexpression : eoi) {
			position++;
			return e;
		} else {
			error
					.push("Failed to parse expression. Trailing garbage at position: "
							+ position);

			position = oldposition;
			return null;
		}
	}

	public AstNode parseIdentifier() {
		consumeWhitespace();

		while (position < input.length()) {
			char codepoint = input.charAt(position);

			if (codepoint == '(') {
				position++;
				return parseExpression(')');
			} else {
				String name = parseName();
				if (name == null) {
					return null;
				}
				AstNode node = environment.getVariable(name);
				if (node == null) {
					error.push("Unknown identifier: " + name);
					return null;
				}
				return node;
			}
		}
		return null;
	}

	String parseName() {
		StringBuilder sb = new StringBuilder();

		while (position < input.length()) {
			char codepoint = input.charAt(position);

			if (sb.length() == 0 ? Character.isJavaIdentifierStart(codepoint)
					: Character.isJavaIdentifierPart(codepoint)) {
				position++;
				sb.append(codepoint);
			} else {
				break;
			}
		}
		return (sb.length() == 0 ? null : sb.toString());
	}

	private AstNode processOperator(Parseable operator, AstNode left, int level) {
		Token[] todo = operator.tasks();
		List<AstNode> params = new ArrayList<AstNode>();

		if (!operator.isPrefixed()) {
			if (left == null)
				return null;
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
		return operator.construct(environment, params, error);
	}

	private void reparse() {
		error.clear();
		position = 0;
		result = parseExpression(null);
		if (result == null) {
			state = Result.Failure;
		} else if (result instanceof AstVoid) {
			state = Result.Executable;
		} else if (result.isInteractome()) {
			state = Result.Interactome;
		} else {
			error
					.push("Expression is not a statement or an interactome query.");
			state = Result.Failure;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Parser: ");
		if (position > 0) {
			sb.append('"');
			sb.append(input.substring(0, position));
			sb.append('"');
		}
		sb.append(" >").append(input.charAt(position)).append("< ");
		if (position < sb.length()) {
			sb.append('"');
			sb.append(input.substring(position + 1));
			sb.append('"');
		}
		return sb.toString();
	}
}
