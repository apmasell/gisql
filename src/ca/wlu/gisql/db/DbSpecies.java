package ca.wlu.gisql.db;

import java.sql.SQLException;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.NamedInteractome;
import ca.wlu.gisql.util.Counter;

public class DbSpecies extends NamedInteractome {

	private static final Logger log = Logger.getLogger(DbSpecies.class);

	private DatabaseManager databaseManager;

	private final int species_id;

	protected DbSpecies(DatabaseManager databaseManager, String species,
			int species_id) {
		super(species, 1, 0.0, Type.Species);
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

	private void addToGenes(Accession accession) throws SQLException {
		Ubergraph ubergraph = Ubergraph.getInstance();
		Counter<Gene> counter = new Counter<Gene>();
		long identifier = accession.getIdentifier();

		databaseManager.pullOrthologs(counter, identifier, species_id);

		if (counter.getTotal() == 0) {
			ubergraph.newGene(accession).setMembership(this, 1);
		} else {
			// TODO MERGE
			for (Entry<Gene, Integer> entry : counter) {
				ubergraph.addOrtholog(entry.getKey(), accession);
				entry.getKey().setMembership(this,
						entry.getValue() / counter.getTotal());
			}
		}
	}

	public int getId() {
		return species_id;
	}

	public boolean prepare() {
		if (databaseManager == null) {
			return true;
		}
		log.info("Loading species " + name);
		try {
			for (Accession accession : databaseManager
					.pullAccessions(species_id)) {
				addToGenes(accession);
			}

			databaseManager.pullInteractions(this);

			log.info("Load complete.");
			databaseManager = null; /* Stop reloading. */
			return true;
		} catch (SQLException e) {
			log.fatal("Failed to load species " + name, e);
			return false;
		}
	}
}
