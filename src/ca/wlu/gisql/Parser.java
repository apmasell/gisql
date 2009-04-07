package ca.wlu.gisql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.BoldIntersection;
import ca.wlu.gisql.interactome.BoundedDifference;
import ca.wlu.gisql.interactome.BoundedSum;
import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.Difference;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Intersection;
import ca.wlu.gisql.interactome.Residuum;
import ca.wlu.gisql.interactome.StrongSymmetricDifference;
import ca.wlu.gisql.interactome.SymmetricDifference;
import ca.wlu.gisql.interactome.ToFile;
import ca.wlu.gisql.interactome.ToVar;
import ca.wlu.gisql.interactome.Union;
import ca.wlu.gisql.util.Parseable;
import ca.wlu.gisql.util.Parseable.NextTask;

public class Parser {
    static final Logger log = Logger.getLogger(Parser.class);

    private static int maxdepth = 0;

    private static Parseable[] operators = new Parseable[] {
	    BoldIntersection.descriptor, BoundedDifference.descriptor,
	    BoundedSum.descriptor, Complement.descriptor,
	    Difference.descriptor, Intersection.descriptor,
	    Residuum.descriptor, StrongSymmetricDifference.descriptor,
	    SymmetricDifference.descriptor, ToFile.descriptor,
	    ToVar.descriptor, Union.descriptor };

    private static Map<Integer, List<Parseable>> otherfixOperators = null;

    private static Map<Integer, List<Parseable>> prefixedOperators = null;

    private static synchronized List<Parseable> getList(
	    Map<Integer, List<Parseable>> map, int level) {
	List<Parseable> list = map.get(level);
	if (list == null) {
	    list = new ArrayList<Parseable>();
	    map.put(level, list);
	}
	return list;
    }

    private static synchronized void prepareParser() {
	if (prefixedOperators != null)
	    return;

	prefixedOperators = new HashMap<Integer, List<Parseable>>();
	otherfixOperators = new HashMap<Integer, List<Parseable>>();
	for (Parseable operator : operators) {
	    int level = operator.getNestingLevel();
	    if (level > maxdepth)
		maxdepth = level;
	    List<Parseable> list = getList(
		    (operator.isPrefixed() ? prefixedOperators
			    : otherfixOperators), level);
	    list.add(operator);
	}

	for (int i = 0; i <= maxdepth; i++) {
	    getList(prefixedOperators, i);
	    getList(otherfixOperators, i);
	}
    }

    private Environment environment;

    private String input;

    private Interactome interactome = null;

    private int position = 0;

    public Parser(Environment environment, String input) {
	this.environment = environment;
	this.input = input;
	prepareParser();
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

    private Interactome parseAutoExpression(int level) {
	Interactome left = null;
	for (Parseable operator : prefixedOperators.get(level)) {
	    if (operator.isMatchingOperator(input.charAt(position))) {
		position++;
		left = processOperator(operator, null, level);
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
		if (operator.isMatchingOperator(input.charAt(position))) {
		    int oldposition = position;
		    position++;
		    Interactome result = processOperator(operator, left, level);
		    if (result != null) {
			left = result;
			matched = true;
			break;
		    }
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

    private Double parseDouble() {
	consumeWhitespace();
	int initialposition = position;
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
	    return new Double(input.substring(initialposition, position));
	} catch (NumberFormatException e) {
	    return null;
	}

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
	    } else if (codepoint == '$') {
		position++;
		String variable = parseName();
		if (variable == null) {
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
		if (species == null)
		    return null;
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
	return (sb.length() == 0 ? null : sb.toString());
    }

    private String parseQuotedString() {
	consumeWhitespace();
	StringBuilder sb = null;

	while (position < input.length()) {
	    char codepoint = input.charAt(position);

	    if (codepoint == '"') {
		position++;
		if (sb == null) {
		    /* first quote. */
		    sb = new StringBuilder();
		} else {
		    /* found final quote. */
		    return (sb.length() == 0 ? null : sb.toString());
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
	return null;
    }

    private Interactome processOperator(Parseable operator, Interactome left,
	    int level) {
	int oldposition = position;
	NextTask[] todo = operator.tasks();
	List<Object> params = new ArrayList<Object>();

	if (!operator.isPrefixed()) {
	    if (left == null)
		return null;
	    params.add(left);
	}
	if (todo != null) {
	    int maybeposition = -1;
	    for (NextTask task : todo) {
		Object nextToken = null;
		consumeWhitespace();
		switch (task) {
		case Maybe:
		    maybeposition = position;
		    continue;
		case Double:
		    nextToken = parseDouble();
		    break;
		case Identifier:
		    nextToken = parseIdentifier();
		    break;
		case Name:
		    nextToken = parseName();
		    break;
		case SubExpression:
		    nextToken = (level == maxdepth ? parseIdentifier()
			    : parseAutoExpression(level + 1));
		    break;
		case QuotedString:
		    nextToken = parseQuotedString();
		}
		if (nextToken != null) {
		    params.add(nextToken);
		} else if (maybeposition != -1) {
		    params.add(nextToken);
		    position = maybeposition;
		} else {
		    position = oldposition;
		    return null;
		}
		maybeposition = -1;
	    }
	}
	return operator.construct(environment, params);
    }

    private void reparse() {
	position = 0;
	interactome = parseExpression(null);
    }
}
