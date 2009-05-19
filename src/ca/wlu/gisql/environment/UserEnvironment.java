package ca.wlu.gisql.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.environment.Environment.EnvironmentListener;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;
import ca.wlu.gisql.interactome.output.FileFormat;

public class UserEnvironment implements Environment, EnvironmentListener {
	private FileFormat format = FileFormat.summary;

	private Interactome last = null;

	private Set<EnvironmentListener> listeners = new HashSet<EnvironmentListener>();

	private TriangularNorm norm;

	private int numCommands = 1;

	private String output = null;

	private Environment parent;

	private Map<String, Interactome> variables = new HashedMap<String, Interactome>();

	public UserEnvironment(Environment parent) {
		super();
		this.parent = parent;
		parent.addListener(this);
	}

	public void addedEnvironmentVariable(String name, Interactome interactome) {
		for (EnvironmentListener listener : listeners) {
			listener.addedEnvironmentVariable(name, interactome);
		}
	}

	public void addListener(EnvironmentListener listener) {
		listeners.add(listener);
	}

	public CachedInteractome append(Interactome interactome) {
		if (interactome == null) {
			return null;
		}
		String name = "$_" + numCommands++;
		CachedInteractome result = CachedInteractome.wrap(interactome, name);
		setVariable(name, result);
		last = result;

		return result;
	}

	public void droppedEnvironmentVariable(String name, Interactome interactome) {
		for (EnvironmentListener listener : listeners) {
			listener.droppedEnvironmentVariable(name, interactome);
		}
	}

	public FileFormat getFormat() {
		return format;
	}

	public Interactome getLast() {
		return last;
	}

	public String getOutput() {
		return output;
	}

	public TriangularNorm getTriangularNorm() {
		return (norm == null ? parent.getTriangularNorm() : norm);
	}

	public Interactome getVariable(String name) {
		Interactome interactome = variables.get(name);
		return (interactome == null ? parent.getVariable(name) : interactome);
	}

	public void lastChanged() {
		for (EnvironmentListener listener : listeners) {
			listener.lastChanged();
		}
	}

	public List<String> names(Type filter) {
		return names(filter, new ArrayList<String>());
	}

	public List<String> names(Type filter, List<String> list) {
		for (Entry<String, Interactome> entry : variables.entrySet()) {
			if (filter == null || filter == Type.Mutable
					|| filter == entry.getValue().getType())
				list.add(entry.getKey());
		}
		if (filter != Type.Mutable)
			parent.names(filter, list);
		return list;
	}

	public void removeListener(EnvironmentListener listener) {
		listeners.remove(listener);
	}

	public void setFormat(FileFormat format) {
		this.format = format;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean setTriangularNorm(TriangularNorm norm) {
		if (norm == null)
			return false;
		this.norm = norm;
		return true;
	}

	public boolean setVariable(String name, Interactome interactome) {
		if (parent.getVariable(name) != null)
			return false;
		Interactome oldinteractome = variables.put(name, interactome);
		for (EnvironmentListener listener : listeners) {
			if (interactome == null) {
				listener.droppedEnvironmentVariable(name, oldinteractome);
			} else {
				listener.addedEnvironmentVariable(name, interactome);
			}
		}
		return true;
	}

	public List<Interactome> variables(Type filter) {
		return variables(filter, new ArrayList<Interactome>());
	}

	public List<Interactome> variables(Type filter, List<Interactome> list) {
		for (Interactome interactome : variables.values()) {
			if (filter == null || filter == Type.Mutable
					|| filter == interactome.getType())
				list.add(interactome);
		}
		if (filter != Type.Mutable)
			parent.variables(filter, list);
		return list;
	}

}
