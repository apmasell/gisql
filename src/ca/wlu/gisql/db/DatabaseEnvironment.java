package ca.wlu.gisql.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.interactome.Interactome;

public class DatabaseEnvironment extends Environment {

	public DatabaseEnvironment(DatabaseManager databaseManager) {
		super(null, false, false);
		Map<Integer, Interactome> speciesById = new HashedMap<Integer, Interactome>();
		for (DbSpecies interactome : databaseManager.getSpecies()) {
			add(interactome.toString(), interactome, Type.InteractomeType);
			speciesById.put(interactome.getId(), interactome);
		}
		putArray("all", new ArrayList<Interactome>(speciesById.values()));
		databaseManager.populateArrays(this, speciesById);
	}

	void putArray(String name, List<Interactome> list) {
		add(name, list, new ListType(Type.InteractomeType));
	}

}
