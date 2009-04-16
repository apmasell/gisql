package ca.wlu.gisql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import ca.wlu.gisql.util.FoldOperator;
import ca.wlu.gisql.util.Parseable;
import ca.wlu.gisql.util.ParseableBinaryOperation;

public class Parser {
    public class Decimal extends NextTask {

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    Double d = parseDouble();
	    if (d == null) {
		error.push("Failed to parse double. Position: " + oldposition);
		return false;
	    }
	    results.add(d);
	    return true;
	}

    }

    public class Expression extends NextTask {

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    Interactome result = (level == maxdepth ? parseIdentifier()
		    : parseAutoExpression(0));
	    if (result == null) {
		error.push("Failed to parse expression. Position: "
			+ oldposition);
		return false;
	    }
	    results.add(result);
	    return true;
	}
    }

    public class ListOf extends NextTask {
	NextTask child;

	char delimiter;

	public ListOf(NextTask child, char delimiter) {
	    super();
	    this.child = child;
	    this.delimiter = delimiter;
	}

	boolean parse(int level, List<Object> results) {
	    List<Object> items = new ArrayList<Object>();

	    if (!child.parse(level, items)) {
		return false;
	    }

	    consumeWhitespace();
	    while (position < input.length()) {
		if (input.charAt(position) == delimiter) {
		    position++;
		    if (!child.parse(level, items)) {
			return false;
		    }
		} else {
		    results.add(items);
		    return true;
		}
		consumeWhitespace();
	    }
	    results.add(items);
	    return true;
	}

    }

    public class Literal extends NextTask {
	char c;

	public Literal(char c) {
	    super();
	    this.c = c;
	}

	boolean parse(int level, List<Object> results) {
	    consumeWhitespace();
	    if (position < input.length() && c == input.charAt(position)) {
		position++;
		return true;
	    }
	    error.push("Expected '" + c + "' missing. Position: " + position);
	    return false;
	}

    }

    public class Maybe extends NextTask {
	NextTask child;

	public Maybe(NextTask child) {
	    super();
	    this.child = child;
	}

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    int errorposition = error.size();
	    if (child.parse(level, results))
		return true;
	    results.add(null);
	    position = oldposition;
	    error.setSize(errorposition);
	    return true;
	}
    }

    public class Name extends NextTask {

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    String name = parseName();
	    if (name == null) {
		error.push("Expected name missing. Position: " + oldposition);
		return false;
	    }
	    results.add(name);
	    return true;
	}

    }

    public abstract class NextTask {
	abstract boolean parse(int level, List<Object> results);
    }

    public class QuotedString extends NextTask {

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    String string = parseQuotedString();
	    if (string == null) {
		error.push("Failed to parse quoted string. Position: "
			+ oldposition);
		return false;
	    }
	    results.add(string);
	    return true;
	}

    }

    public class SubExpression extends NextTask {

	boolean parse(int level, List<Object> results) {
	    Interactome result = (level == maxdepth ? parseIdentifier()
		    : parseAutoExpression(level + 1));
	    if (result == null)
		return false;
	    results.add(result);
	    return true;
	}
    }

    public class Word extends NextTask {
	private String word;

	public Word(String word) {
	    this.word = word;
	}

	boolean parse(int level, List<Object> results) {
	    int oldposition = position;
	    String name = parseName();
	    if (name == null || !word.equals(name)) {
		error.push("Expected " + word + " missing. Position: "
			+ oldposition);
		return false;
	    }
	    return true;
	}

    }

    private static String help;

    static final Logger log = Logger.getLogger(Parser.class);

    private static int maxdepth = 0;

    private static Parseable[] operators = new Parseable[] {
	    BoldIntersection.descriptor, BoundedDifference.descriptor,
	    BoundedSum.descriptor, Complement.descriptor,
	    Difference.descriptor, Environment.clearDescriptor,
	    Environment.runDescriptor, Environment.variableDescriptor,
	    Intersection.descriptor, Residuum.descriptor,
	    StrongSymmetricDifference.descriptor,
	    SymmetricDifference.descriptor, ToFile.descriptor,
	    ToVar.descriptor, Union.descriptor };

    private static Map<Integer, List<Parseable>> otherfixOperators = null;

    private static Map<Integer, List<Parseable>> prefixedOperators = null;

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
	    if (operator instanceof ParseableBinaryOperation) {
		getList(prefixedOperators, level).add(
			new FoldOperator((ParseableBinaryOperation) operator));
	    }
	}

	StringBuilder sb = new StringBuilder();
	sb
		.append("Syntax Help\nEach operator and it's membership function is described from lowest prescedence to highest.\n\n");
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
	sb
		.append("\nAny other word will be interpreted as a species identifier and read from the database.\nParentheses may be used to control order of operations.");
	help = sb.toString();
    }

    private Environment environment;

    private Stack<String> error = new Stack<String>();

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

    public String getErrors() {
	StringBuilder sb = new StringBuilder();
	while (error.size() > 0) {
	    sb.append(error.pop());
	    sb.append('\n');
	}
	return sb.toString();
    }

    private Interactome parseAutoExpression(int level) {
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
		String species = parseName();
		if (species == null) {
		    return null;
		}
		Interactome i = environment.getSpeciesInteractome(species);
		if (i == null) {
		    error.push("Unknown species: " + species);
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
		if (position < input.length()) {
		    sb.append(input.charAt(position));
		    position++;
		} else {
		    return null;
		}
	    } else {
		if (sb == null) {
		    return null;
		} else {
		    position++;

		    sb.append(codepoint);
		}
	    }
	}
	return null;
    }

    private Interactome processOperator(Parseable operator, Interactome left,
	    int level) {
	Parser.NextTask[] todo = operator.tasks(this);
	List<Object> params = new ArrayList<Object>();

	if (!operator.isPrefixed()) {
	    if (left == null)
		return null;
	    params.add(left);
	}
	if (todo != null) {
	    for (Parser.NextTask task : todo) {
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
