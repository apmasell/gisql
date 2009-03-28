package ca.wlu.gisql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Complement;
import ca.wlu.gisql.interactome.Difference;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Intersection;
import ca.wlu.gisql.interactome.SymmetricDifference;
import ca.wlu.gisql.interactome.Union;

public class Environment implements TreeModel {

    static final Logger log = Logger.getLogger(Environment.class);

    private DatabaseManager dm;

    private String input;

    private Interactome last = null;

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    private int position;

    private final String treeLast = "Last Result";

    private final String treeRoot = "Environment";

    private final String treeSpecies = "Species";

    private final String treeVariables = "Variables";

    private Map<String, Interactome> variables = new HashMap<String, Interactome>();

    public Environment(DatabaseManager dm) {
	this.dm = dm;
    }

    public void addTreeModelListener(TreeModelListener listener) {
	listeners.add(listener);
    }

    public void clearVariables() {
	variables.clear();
	notifyListeners();
    }

    public void consumeWhitespace() {
	while (position < input.length()
		&& Character.isWhitespace(input.charAt(position))) {
	    position++;
	}
    }

    public Object getChild(Object item, int index) {
	if (item == treeRoot) {
	    switch (index) {
	    case 0:
		return treeSpecies;
	    case 1:
		return treeVariables;
	    case 2:
		return treeLast;
	    }
	} else if (item == treeSpecies) {
	    return dm.getSpeciesName(index);
	} else if (item == treeVariables) {
	    for (String name : variables.keySet()) {
		if (index == 0) {
		    return name;
		}
		index--;
	    }
	}
	return null;
    }

    public int getChildCount(Object item) {

	if (item == treeRoot) {
	    return 3;
	} else if (item == treeSpecies) {
	    return dm.sizeSpecies();
	} else if (item == treeVariables) {
	    return variables.size();
	} else {
	    return 0;
	}
    }

    public int getIndexOfChild(Object arg0, Object arg1) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Interactome getInteractome(TreePath tp) {
	if (tp == null) {
	    return null;
	}
	if (tp.getPathCount() == 2 && tp.getPathComponent(1) == treeLast) {
	    return last;
	} else if (tp.getPathCount() == 3) {
	    if (tp.getPathComponent(1) == treeSpecies) {
		return dm
			.getSpeciesInteractome((String) tp.getPathComponent(2));
	    } else if (tp.getPathComponent(1) == treeVariables) {
		return this.getVariable((String) tp.getPathComponent(2));
	    }
	}
	return null;
    }

    public Interactome getLast() {
	return last;
    }

    public Object getRoot() {
	return treeRoot;
    }

    public Interactome getVariable(String name) {
	return variables.get(name);
    }

    public boolean isLeaf(Object item) {
	if (item == treeRoot || item == treeSpecies || item == treeVariables) {
	    return false;
	} else {
	    return true;
	}
    }

    private void notifyListeners() {
	for (TreeModelListener tml : listeners) {
	    tml.treeStructureChanged(new TreeModelEvent(this,
		    new Object[] { treeRoot }));
	}
    }

    public Interactome parse(String input) {
	position = 0;
	this.input = input;
	Interactome result = parseExpression(true);
	this.input = null;
	notifyListeners();
	if (result != null) {
	    last = result;
	}
	return result;
    }

    public Interactome parseAndExpression() {
	Interactome left = parseIdentifier();

	if (left == null) {
	    return null;
	}
	while (position < input.length()) {
	    consumeWhitespace();

	    char codepoint = input.charAt(position);

	    if (codepoint == '^' || codepoint == 'Δ') {
		position++;
		Interactome right = parseIdentifier();
		if (right == null) {
		    return null;
		}
		left = new SymmetricDifference(left, right);
	    } else if (codepoint == '&' || codepoint == '\u2229') {
		position++;
		Interactome right = parseIdentifier();
		if (right == null) {
		    return null;
		}
		left = new Intersection(left, right);
	    } else {
		return left;
	    }
	}
	return left;
    }

    private Interactome parseDiffExpression() {
	Interactome left = parseOrExpression();

	if (left == null) {
	    return null;
	}
	while (position < input.length()) {
	    consumeWhitespace();

	    char codepoint = input.charAt(position);

	    if (codepoint == '-' || codepoint == '∖') {
		position++;
		Interactome right = parseOrExpression();
		if (right == null) {
		    return null;
		}
		left = new Difference(left, right);
	    } else {
		return left;
	    }
	}
	return left;
    }

    private Interactome parseExpression(boolean toplevel) {
	Interactome e = parseDiffExpression();

	if (e == null) {
	    return null;
	}
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
			} else if (codepoint == '!' || codepoint == '¬') {
			    position ++;
			    Interactome value = parseIdentifier();
			    if (value == null) {
				return null;
			    }
			    return new Complement(value);
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

	if (left == null) {
	    return null;
	}
	while (position < input.length()) {
	    consumeWhitespace();

	    char codepoint = input.charAt(position);

	    if (codepoint == '|' || codepoint == '\u222A') {
		position++;
		Interactome right = parseAndExpression();
		if (right == null) {
		    return null;
		}
		left = new Union(left, right);
	    } else {
		return left;
	    }
	}
	return left;
    }

    public void removeTreeModelListener(TreeModelListener listener) {
	listeners.remove(listener);
    }

    public void setVariable(String name, Interactome value) {
	variables.put(name, value);
	notifyListeners();
    }

    public void valueForPathChanged(TreePath arg0, Object arg1) {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
