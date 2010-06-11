package ca.wlu.gisql.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;

import ca.wlu.gisql.ast.type.ArrowType;
import ca.wlu.gisql.ast.type.ListType;
import ca.wlu.gisql.ast.type.MaybeType;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionRunner;

public class DatabaseEnvironment extends Environment {

	public static final class Interactome2TaxId implements GenericFunction {
		private static final Type type = new ArrowType(Type.InteractomeType,
				new MaybeType(Type.NumberType));

		public Interactome2TaxId(ExpressionRunner runner) {
		}

		@Override
		public String getDescription() {
			return "Find the NCBI taxon identifier of an interactome, if one exists.";
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public Object run(Object... parameters) {
			Interactome interactome = (Interactome) parameters[0];
			return interactome instanceof DbSpecies ? ((DbSpecies) interactome)
					.getId() : null;
		}

		@Override
		public String toString() {
			return "taxid2species";
		}
	}

	public static final class TaxId2Interactome implements GenericFunction {
		private static final Type type = new ArrowType(Type.NumberType,
				new MaybeType(Type.InteractomeType));
		private final ExpressionRunner runner;

		public TaxId2Interactome(ExpressionRunner runner) {
			this.runner = runner;
		}

		@Override
		public String getDescription() {
			return "Find the interactome of the corresponding NCBI taxon identifier.";
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public Object run(Object... parameters) {
			long taxid = (Long) parameters[0];
			ArrayList<?> all = (ArrayList<?>) runner.getEnvironment()
					.getVariable("all");
			for (Object interactome : all) {
				if (interactome instanceof DbSpecies
						&& ((DbSpecies) interactome).getId() == taxid) {
					return interactome;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return "interactome2taxid";
		}
	}

	public DatabaseEnvironment(DatabaseManager databaseManager) {
		super(null, false, false);
		Map<Long, Interactome> speciesById = new HashedMap<Long, Interactome>();
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
