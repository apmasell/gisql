package ca.wlu.gisql.db;

import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.ast.AstInteractome;
import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class DatabaseEnvironment extends Environment {

	public DatabaseEnvironment(DatabaseManager databaseManager) {
		super(null, false, false);
		Map<Integer, AstNode> speciesById = new HashedMap<Integer, AstNode>();
		for (DbSpecies interactome : databaseManager.getSpecies()) {
			AstNode node = new AstInteractome(interactome);
			this.add(interactome.toString(), node);
			speciesById.put(interactome.getId(), node);
		}
		databaseManager.populateArrays(this, speciesById);
	}

	void putArray(String name, AstList list) {
		this.add(name, list);
	}

}
