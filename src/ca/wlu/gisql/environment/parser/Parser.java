package ca.wlu.gisql.environment.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ca.wlu.gisql.environment.ClearFunction;
import ca.wlu.gisql.environment.EchoFunction;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.FormatFunction;
import ca.wlu.gisql.environment.LastInteractome;
import ca.wlu.gisql.environment.OutputFunction;
import ca.wlu.gisql.environment.RunFunction;
import ca.wlu.gisql.environment.parser.util.FoldOperator;
import ca.wlu.gisql.environment.parser.util.ParseableBinaryOperation;
import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.Cut;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ToVar;
import ca.wlu.gisql.interactome.binary.BoldIntersection;
import ca.wlu.gisql.interactome.binary.BoundedDifference;
import ca.wlu.gisql.interactome.binary.BoundedSum;
import ca.wlu.gisql.interactome.binary.Difference;
import ca.wlu.gisql.interactome.binary.Intersection;
import ca.wlu.gisql.interactome.binary.Residuum;
import ca.wlu.gisql.interactome.binary.StrongSymmetricDifference;
import ca.wlu.gisql.interactome.binary.SymmetricDifference;
import ca.wlu.gisql.interactome.binary.Union;
import ca.wlu.gisql.interactome.output.AbstractOutput;

public class Parser {
	private static String help;

	static int maxdepth = 0;

	private final static Parseable[] operators = new Parseable[] {
			AbstractOutput.descriptor, BoldIntersection.descriptor,
			BoundedDifference.descriptor, BoundedSum.descriptor,
			ClearFunction.descriptor, Complement.descriptor, Cut.descriptor,
			Difference.descriptor, EchoFunction.descriptor,
			FormatFunction.descriptor, Intersection.descriptor,
			LastInteractome.descriptor, OutputFunction.descriptor,
			Residuum.descriptor, RunFunction.descriptor,
			StrongSymmetricDifference.descriptor,
			SymmetricDifference.descriptor, ToVar.descriptor, Union.descriptor };

	private static Map<Integer, List<Parseable>> otherfixOperators = null;

	private static Map<Integer, List<Parseable>> prefixedOperators = null;

	public static synchronized void addParseable(Parseable operator) {
		prepareParser();
		installOperator(operator);
		buildHelp();
	}

	private static void buildHelp() {
		StringBuilder sb = new StringBuilder();
		sb
				.append("Syntax Help\nEach operator and it's membership function is described from lowest precedence to highest.\n\n");
		/* This also initialises every entry in the maps. */
		for (int level = 0; level <= maxdepth; level++) {
			for (Parseable operator : getList(prefixedOperators, level)) {
				operator.show(sb).append('\n');
			}
			for (Parseable operator : getList(otherfixOperators, level)) {
				operator.show(sb).append('\n');
			}
			sb.append('\n');

		}
		sb.append("Lists may be any of the following:\n");
		ListExpression.show(sb);
		sb
				.append("\nAny other word will be interpreted as a identifier for a species or variable.\nParentheses may be used to control order of operations.");
		help = sb.toString();

	}

	public static String getHelp() {
		prepareParser();
		return help;
	}

	private static synchronized List<Parseable> getList(
			Map<Integer, List<Parseable>> map, int level) {
		List<Parseable> list = map.get(level);
		if (list == null) {
			list = new ArrayList<Parseable>();
			map.put(level, list);
		}
		return list;
	}

	private static void installOperator(Parseable operator) {
		int level = operator.getNestingLevel();
		if (level > maxdepth)
			maxdepth = level;
		List<Parseable> list = getList(
				(operator.isPrefixed() ? prefixedOperators : otherfixOperators),
				level);
		if (list.contains(operator))
			return;
		list.add(operator);
		if (operator instanceof ParseableBinaryOperation) {
			getList(prefixedOperators, level).add(
					new FoldOperator((ParseableBinaryOperation) operator));
		}
	}

	private static synchronized void prepareParser() {
		if (prefixedOperators != null)
			return;

		prefixedOperators = new HashMap<Integer, List<Parseable>>();
		otherfixOperators = new HashMap<Integer, List<Parseable>>();
		for (Parseable operator : operators) {
			installOperator(operator);

		}
		buildHelp();
	}

	Environment environment;

	final Stack<String> error = new Stack<String>();

	final String input;

	private Interactome interactome = null;

	int position = 0;

	public Parser(Environment environment, String input) {
		this.environment = environment;
		this.input = input;
		prepareParser();
		reparse();
	}

	void consumeWhitespace() {
		while (position < input.length()
				&& Character.isWhitespace(input.charAt(position))) {
			position++;
		}
	}

	public Interactome get() {
		return interactome;
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

	Interactome parseAutoExpression(int level) {
		Interactome left = null;
		if (position >= input.length())
			return null;

		int errorposition = error.size();
		for (Parseable operator : prefixedOperators.get(level)) {
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
			left = (level >= maxdepth ? parseIdentifier()
					: parseAutoExpression(level + 1));

		if (left == null) {
			return null;
		}

		consumeWhitespace(); /* Do this before testing input length. */
		while (position < input.length()) {
			boolean matched = false;
			for (Parseable operator : otherfixOperators.get(level)) {
				int oldposition = position;
				errorposition = error.size();
				if (operator.isMatchingOperator(input.charAt(position))) {
					position++;
					Interactome result = processOperator(operator, left, level);
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

	private Interactome parseExpression(Character endofexpression) {
		Interactome e = parseAutoExpression(0);

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

	public Interactome parseIdentifier() {
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
				Interactome i = environment.getVariable(name);
				if (i == null) {
					error.push("Unknown identifier: " + name);
					return null;
				}
				return i;
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

	private Interactome processOperator(Parseable operator, Interactome left,
			int level) {
		Token[] todo = operator.tasks(this);
		List<Object> params = new ArrayList<Object>();

		if (!operator.isPrefixed()) {
			if (left == null)
				return null;
			params.add(left);
		}
		if (todo != null) {
			for (Token task : todo) {
				consumeWhitespace();
				if (!task.parse(level, params)) {
					return null;
				}
			}
		}
		return operator.construct(environment, params, error);
	}

	private void reparse() {
		error.clear();
		position = 0;
		interactome = parseExpression(null);
	}
}
