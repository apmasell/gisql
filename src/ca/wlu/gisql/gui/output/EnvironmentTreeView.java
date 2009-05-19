package ca.wlu.gisql.gui.output;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.Environment.EnvironmentListener;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;

public class EnvironmentTreeView implements TreeModel, EnvironmentListener {

	private Environment environment;

	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	private final String treeLast = "Last Result";

	private final String treeRoot = "Environment";

	private final String treeSpecies = "Species";

	private final String treeVariables = "Variables";

	public EnvironmentTreeView(Environment environment) {
		super();
		this.environment = environment;
		environment.addListener(this);
	}

	public void addedEnvironmentVariable(String name, Interactome interactome) {
		notifyListeners();
	}

	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

	public void droppedEnvironmentVariable(String name, Interactome interactome) {
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
			return environment.variables(Type.Species).get(index);
		} else if (item == treeVariables) {
			return environment.variables(Type.Computed).get(index);
		}
		return null;
	}

	public int getChildCount(Object item) {

		if (item == treeRoot) {
			return (environment.getLast() == null ? 2 : 3);
		} else if (item == treeSpecies) {
			return environment.variables(Type.Species).size();
		} else if (item == treeVariables) {
			return environment.variables(Type.Computed).size();
		} else {
			return 0;
		}
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == treeRoot) {
			if (child == treeSpecies) {
				return 0;
			} else if (child == treeVariables) {
				return 1;
			} else if (child == treeLast) {
				return 2;
			}
		} else if (parent == treeSpecies) {
			return environment.variables(Type.Species).indexOf(child);
		} else if (parent == treeVariables) {
			return environment.variables(Type.Computed).indexOf(child);
		}
		return -1;
	}

	public Interactome getInteractome(TreePath tp) {
		if (tp == null) {
			return null;
		}
		if (tp.getPathCount() == 2 && tp.getPathComponent(1) == treeLast) {
			return environment.getLast();
		} else if (tp.getPathCount() == 3) {
			if (tp.getPathComponent(1) == treeSpecies
					|| tp.getPathComponent(1) == treeVariables) {
				return (Interactome) tp.getPathComponent(2);
			}
		}
		return null;
	}

	public Object getRoot() {
		return treeRoot;
	}

	public boolean isLeaf(Object item) {
		if (item == treeRoot || item == treeSpecies || item == treeVariables) {
			return false;
		} else {
			return true;
		}
	}

	public void lastChanged() {
		notifyListeners();
	}

	private void notifyListeners() {
		TreeModelEvent event = new TreeModelEvent(this,
				new Object[] { treeRoot });
		for (TreeModelListener tml : listeners) {
			tml.treeStructureChanged(event);
		}
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	public void valueForPathChanged(TreePath path, Object value) {
		throw new UnsupportedOperationException();
	}

}
