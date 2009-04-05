package ca.wlu.gisql;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.Difference;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Intersection;
import ca.wlu.gisql.interactome.SymmetricDifference;
import ca.wlu.gisql.interactome.ToFile;
import ca.wlu.gisql.interactome.ToVar;
import ca.wlu.gisql.interactome.Union;

public class Parser {
    static final Logger log = Logger.getLogger(Parser.class);

    private Environment environment;

    private String input;

    private Interactome interactome = null;

    private int position = 0;

    public Parser(Environment environment, String input) {
	this.environment = environment;
	this.input = input;

	reparse();
    }

    public void consumeWhitespace() {
	while (position < input.length()
		&& Character.isWhitespace(input.charAt(position))) {
	    position++;
	}
    }

    public Interactome get() {
	return interactome;
    }

    private Interactome parseAndExpression() {
	Interactome left = parseIdentifier();

	if (left == null) {
	    return null;
	}
	int oldposition = position;
	consumeWhitespace();
	while (position < input.length()) {

	    char codepoint = input.charAt(position);

	    if (codepoint == '^' || codepoint == '∆') {
		position++;
		Interactome right = parseIdentifier();
		if (right == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		left = new SymmetricDifference(left, right);
	    } else if (codepoint == '&' || codepoint == '\u2229') {
		position++;
		Interactome right = parseIdentifier();
		if (right == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		left = new Intersection(left, right);
	    } else {
		return left;
	    }
	    consumeWhitespace();
	}
	return left;
    }

    private Interactome parseDiffExpression() {
	Interactome left = parseOrExpression();

	if (left == null) {
	    return null;
	}
	int oldposition = position;
	consumeWhitespace();
	while (position < input.length()) {

	    char codepoint = input.charAt(position);

	    if (codepoint == '-' || codepoint == '∖') {
		position++;
		Interactome right = parseOrExpression();
		if (right == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		left = new Difference(left, right);
	    } else {
		return left;
	    }
	    consumeWhitespace();
	}
	return left;
    }

    private Interactome parseExpression(Character endofexpression) {
	Interactome e = parseRedirectExpression();

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
	    position = oldposition;
	    return null;
	}
    }

    private String parseFilename() {
	consumeWhitespace();
	StringBuilder sb = null;
	int oldposition = position;

	while (position < input.length()) {
	    char codepoint = input.charAt(position);

	    if (codepoint == '"') {
		position++;
		if (sb == null) {
		    /* first quote. */
		    sb = new StringBuilder();
		} else {
		    /* found final quote. */
		    return sb.toString();
		}
	    } else if (codepoint == '\\') {
		position++;
		sb.append(input.charAt(position));
		position++;
	    } else {
		position++;
		sb.append(codepoint);
	    }
	}
	position = oldposition;
	return null;
    }

    public Interactome parseIdentifier() {
	consumeWhitespace();

	while (position < input.length()) {
	    char codepoint = input.charAt(position);

	    if (codepoint == '(') {
		position++;
		return parseExpression(')');
	    } else if (codepoint == '!' || codepoint == '¬') {
		position++;
		Interactome value = parseIdentifier();
		if (value == null) {
		    return null;
		}
		return new Complement(value);
	    } else if (codepoint == '$') {
		position++;
		String variable = parseName();
		if (variable == null || variable.trim().length() == 0) {
		    log.fatal("Expected variable name after $. Position: "
			    + position);
		    return null;
		}
		Interactome value = environment.getVariable(variable);
		if (value == null) {
		    log.fatal("Variable " + variable + " is undefined.");
		    return null;
		}
		return value;
	    } else {
		String species = parseName();
		Interactome i = environment.getSpeciesInteractome(species);
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

    public Double parseMaybeDouble() {
	consumeWhitespace();
	int oldposition = position;
	while (position < input.length()
		&& Character.isDigit(input.charAt(position))) {
	    position++;
	}
	if (position < input.length() && input.charAt(position) == '.') {
	    position++;
	    while (position < input.length()
		    && Character.isDigit(input.charAt(position))) {
		position++;
	    }
	}

	try {
	    return new Double(input.substring(oldposition, position));
	} catch (NumberFormatException e) {
	    position = oldposition;
	    return null;
	}

    }

    private String parseName() {
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
	return sb.toString();
    }

    private Interactome parseOrExpression() {
	Interactome left = parseAndExpression();

	if (left == null) {
	    return null;
	}
	int oldposition = position;
	while (position < input.length()) {
	    consumeWhitespace();

	    char codepoint = input.charAt(position);

	    if (codepoint == '|' || codepoint == '\u222A') {
		position++;
		Interactome right = parseAndExpression();
		if (right == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		left = new Union(left, right);
	    } else {
		return left;
	    }
	}
	return left;
    }

    private Interactome parseRedirectExpression() {
	Interactome left = parseDiffExpression();

	if (left == null) {
	    return null;
	}
	int oldposition = position;
	consumeWhitespace();
	while (position < input.length()) {

	    char codepoint = input.charAt(position);

	    if (codepoint == '>' || codepoint == '→') {
		position++;
		Double lowerbound = parseMaybeDouble();
		Double upperbound;

		if (lowerbound != null) {
		    upperbound = parseMaybeDouble();
		    if (upperbound == null) {
			position = oldposition;
			return left;
		    }
		} else {
		    lowerbound = 0.0;
		    upperbound = 1.0;
		}
		String filename = parseFilename();
		if (filename == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		// TODO Support other formats
		left = new ToFile(left, ToFile.FORMAT_INTERACTOME_TEXT,
			filename, lowerbound, upperbound);
	    } else if (codepoint == '@' || codepoint == '≝') {
		position++;
		consumeWhitespace();
		String varname = parseName();
		if (varname == null) {
		    position = oldposition;
		    return left;
		}
		oldposition = position;
		left = new ToVar(environment, left, varname);
	    } else {
		return left;
	    }
	    consumeWhitespace();
	}
	return left;
    }

    private void reparse() {
	position = 0;
	interactome = parseExpression(null);
    }

}
