package ca.wlu.gisql.db;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.NamedInteractome;

public class DbSpecies extends NamedInteractome implements TaxonomicInteractome {

	private DatabaseManager databaseManager;

	private final Logger log = Logger.getLogger(DbSpecies.class);

	private final long species_id;

	protected DbSpecies(DatabaseManager databaseManager, String species,
			long species_id) {
		super(species, 0.0, Construction.Species, true);
		this.species_id = species_id;
		this.databaseManager = databaseManager;
	}

	protected void addInteraction(long identifier1, long identifier2,
			double membership) {
		for (Interaction interaction : Ubergraph.getInstance()
				.upsertInteraction(identifier1, identifier2)) {
			interaction.setMembership(this, membership);
		}
	}

	public long getId() {
		return species_id;
	}

	@Override
	public boolean prepare() {
		if (databaseManager == null) {
			return true;
		}
		log.info("Loading species " + name);
		try {
			databaseManager.pullGenes(this);
			databaseManager.pullInteractions(this);

			log.info("Loading " + name + " complete.");
			databaseManager = null; /* Stop reloading. */
			return true;
		} catch (SQLException e) {
			log.fatal("Failed to load species " + name, e);
			return false;
		}
	}
}
