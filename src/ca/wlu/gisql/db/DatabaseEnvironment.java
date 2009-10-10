package ca.wlu.gisql.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.ParserEnvironment;
import ca.wlu.gisql.interactome.Interactome;

public class DatabaseEnvironment extends Environment {

	public DatabaseEnvironment(DatabaseManager databaseManager) {
		super(ParserEnvironment.self, false, false);
		Map<Integer, Interactome> speciesById = new HashedMap<Integer, Interactome>();
		for (DbSpecies interactome : databaseManager.getSpecies()) {
			AstNode node = new AstLiteral(Type.InteractomeType, interactome);
			add(interactome.toString(), node);
			speciesById.put(interactome.getId(), interactome);
		}
		putArray("all", new ArrayList<Interactome>(speciesById.values()));
		databaseManager.populateArrays(this, speciesById);
	}

	void putArray(String name, List<Interactome> list) {
		add(name, new AstLiteral(new ListType(Type.InteractomeType), list));
	}

}
