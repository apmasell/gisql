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
		Map<Long, TaxonomicInteractome> speciesById = new HashedMap<Long, TaxonomicInteractome>();
		for (DbSpecies interactome : databaseManager.getSpecies()) {
			add(interactome.toString(), interactome, Type.InteractomeType);
			speciesById.put(interactome.getId(), interactome);
		}
		putArray("all", new ArrayList<Interactome>(speciesById.values()));
		databaseManager.populateArrays(this, speciesById);
		add("taxid2species", new TaxId2Interactome(null),
				TaxId2Interactome.type);
		add("interactome2taxid", new Interactome2TaxId(null),
				Interactome2TaxId.type);
	}

	void putArray(String name, List<Interactome> list) {
		add(name, list, new ListType(Type.InteractomeType));
	}

}
