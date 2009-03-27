package ca.wlu.gisql;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Database;
import ca.wlu.gisql.interactome.Difference;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Intersection;
import ca.wlu.gisql.interactome.SymmetricDifference;
import ca.wlu.gisql.interactome.Union;

public class Environment {
	static final Logger log = Logger.getLogger(Database.class);

	private DatabaseManager dm;

	private String input;

	private int position;

	private Map<String, Interactome> variables = new HashMap<String, Interactome>();

	public Environment(DatabaseManager dm) {
		this.dm = dm;
	}

	public void clearVariables() {
		variables.clear();
	}

	public Interactome getVariable(String name) {
		return variables.get(name);
	}

	public void setVariable(String name, Interactome value) {
		variables.put(name, value);
	}

	public void consumeWhitespace() {
		while (position < input.length()
				&& Character.isWhitespace(input.charAt(position))) {
			position++;
		}
	}

	public Interactome parse(String input) {
		position = 0;
		this.input = input;
		Interactome result = parseExpression(true);
		this.input = null;
		return result;
	}

	public Interactome parseAndExpression() {
		Interactome left = parseIdentifier();

		if (left == null)
			return null;

		while (position < input.length()) {
			consumeWhitespace();

			char codepoint = input.charAt(position);

			if (codepoint == '^' || codepoint == 'Δ') {
				position++;
				Interactome right = parseIdentifier();
				if (right == null)
					return null;
				left = new SymmetricDifference(left, right);
			} else if (codepoint == '&' || codepoint == '\u2229') {
				position++;
				Interactome right = parseIdentifier();
				if (right == null)
					return null;
				left = new Intersection(left, right);
			} else {
				return left;
			}
		}
		return left;
	}

	private Interactome parseDiffExpression() {
		Interactome left = parseOrExpression();

		if (left == null)
			return null;

		while (position < input.length()) {
			consumeWhitespace();

			char codepoint = input.charAt(position);

			if (codepoint == '-') {
				position++;
				Interactome right = parseOrExpression();
				if (right == null)
					return null;
				left = new Difference(left, right);
			} else {
				return left;
			}
		}
		return left;
	}

	private Interactome parseExpression(boolean toplevel) {
		Interactome e = parseDiffExpression();

		if (e == null)
			return null;

		while (position < input.length()) {
			consumeWhitespace();

			char codepoint = input.charAt(position);

			if (!toplevel && codepoint == ')') {
				return e;
			} else {
				log.fatal("Unexpected character " + codepoint + " at position "
						+ position);
				return null;
			}
		}
		if (toplevel && position == input.length()) {
			return e;
		} else {
			log.fatal("Unexpected end of input.");
			return null;
		}
	}

	public Interactome parseIdentifier() {
		consumeWhitespace();

		while (position < input.length()) {
			char codepoint = input.charAt(position);

			if (codepoint == '(') {
				position++;
				return parseExpression(false);
			} else if (codepoint == '$') {
				position++;
				String variable = parseName();
				if (variable == null || variable.isEmpty()) {
					log.fatal("Expected variable name after $. Position: "
							+ position);
					return null;
				}
				Interactome value = variables.get(variable);
				if (value == null) {
					log.fatal("Variable " + variable + " is undefined.");
					return null;
				}
				return value;
			} else {
				String species = parseName();
				Interactome i = dm.getSpeciesInteractome(species);
				if (i == null) {
					log.fatal("The species " + species
							+ " does not exist in the database.");
					return null;
				}
				return i;
			}
		}
		return null;
	}

	private String parseName() {
		StringBuilder sb = new StringBuilder();

		while (position < input.length()) {
			char codepoint = input.charAt(position);
			if (Character.isJavaIdentifierPart(codepoint)) {
				position++;
				sb.append(codepoint);
			} else {
				break;
			}
		}
		return sb.toString();
	}

	private Interactome parseOrExpression() {
		Interactome left = parseAndExpression();

		if (left == null)
			return null;

		while (position < input.length()) {
			consumeWhitespace();

			char codepoint = input.charAt(position);

			if (codepoint == '|' || codepoint == '\u222A') {
				position++;
				Interactome right = parseAndExpression();
				if (right == null)
					return null;
				left = new Union(left, right);
			} else {
				return left;
			}
		}
		return left;
	}

}
