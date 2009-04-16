package ca.wlu.gisql;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import ca.wlu.gisql.fuzzy.Godel;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Parseable;

public class Environment implements TreeModel {

    public static final Parseable descriptor = new Parseable() {

	public Interactome construct(Environment environment,
		List<Object> params, Stack<String> error) {
	    String name = (String) params.get(0);
	    Interactome result = environment.getVariable(name);
	    if (result == null)
		error.push("Undefined variable: " + name);
	    return result;
	}

	public int getNestingLevel() {
	    return 6;
	}

	public boolean isMatchingOperator(char c) {
	    return c == '$';
	}

	public boolean isPrefixed() {
	    return true;
	}

	public PrintStream show(PrintStream print) {
	    print.print("Read a variable: $varname");
	    return print;
	}

	public StringBuilder show(StringBuilder sb) {
	    sb.append("Read a variable: $varname");
	    return sb;
	}

	public Parser.NextTask[] tasks(Parser parser) {
	    return new Parser.NextTask[] { parser.new Name() };
	}

    };

    static final Logger log = Logger.getLogger(Environment.class);

    private DatabaseManager dm;

    private Interactome last = null;

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    private int numCommands = 1;

    private final String treeLast = "Last Result";

    private final String treeRoot = "Environment";

    private final String treeSpecies = "Species";

    private final String treeVariables = "Variables";

    private Map<String, Interactome> variables = new HashMap<String, Interactome>();

    private TriangularNorm norm = new Godel();

    public Environment(DatabaseManager dm) {
	this.dm = dm;
    }

    public void addTreeModelListener(TreeModelListener listener) {
	listeners.add(listener);
    }

    public void append(Interactome interactome) {
	if (interactome != null) {
	    setVariable("_" + numCommands++, interactome);
	}
    }

    public void clearVariables() {
	variables.clear();
	notifyListeners();
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

    public Interactome getSpeciesInteractome(String species) {
	return dm.getSpeciesInteractome(species);
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

    public void removeTreeModelListener(TreeModelListener listener) {
	listeners.remove(listener);
    }

    public void setVariable(String name, Interactome value) {
	variables.put(name, value);
	notifyListeners();
    }

    public void valueForPathChanged(TreePath path, Object value) {
	throw new UnsupportedOperationException();
    }

    public void setNorm(TriangularNorm norm) {
	this.norm = norm;
    }

    public TriangularNorm getNorm() {
	return norm;
    }

}
