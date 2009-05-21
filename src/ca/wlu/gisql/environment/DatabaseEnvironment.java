package ca.wlu.gisql.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.db.DbSpecies;
import ca.wlu.gisql.fuzzy.Godel;
import ca.wlu.gisql.fuzzy.TriangularNorm;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.Interactome.Type;

public class DatabaseEnvironment implements Environment {
	private static TriangularNorm norm = new Godel();

	private final Set<EnvironmentListener> listeners = new HashSet<EnvironmentListener>();

	private final Map<String, DbSpecies> species = new HashedMap<String, DbSpecies>();

	public DatabaseEnvironment(DatabaseManager databaseManager) {
		super();
		for (DbSpecies interactome : databaseManager.getSpecies()) {
			species.put(interactome.toString(), interactome);
		}
	}

	public void addListener(EnvironmentListener listener) {
		listeners.add(listener);
	}

	public Interactome getLast() {
		return null;
	}

	public TriangularNorm getTriangularNorm() {
		return norm;
	}

	public Interactome getVariable(String name) {
		return species.get(name);
	}

	public List<String> names(Type filter) {
		return names(filter, new ArrayList<String>());
	}

	public List<String> names(Type filter, List<String> list) {
		if (filter == Type.Species) {
			list.addAll(species.keySet());
		}
		return list;
	}

	public void removeListener(EnvironmentListener listener) {
		listeners.remove(listener);
	}

	public boolean setTriangularNorm(TriangularNorm norm) {
		return false;
	}

	public boolean setVariable(String name, Interactome interactome) {
		return false;
	}

	public List<Interactome> variables(Type filter) {
		return variables(filter, new ArrayList<Interactome>());
	}

	public List<Interactome> variables(Type filter, List<Interactome> list) {
		if (filter == Type.Species) {
			list.addAll(species.values());
		}
		return list;
	}

}
