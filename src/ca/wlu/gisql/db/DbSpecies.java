package ca.wlu.gisql.db;

import java.sql.SQLException;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.NamedInteractome;
import ca.wlu.gisql.util.CliqueMerger;
import ca.wlu.gisql.util.Counter;
import ca.wlu.gisql.util.CliqueMerger.Master;

public class DbSpecies extends NamedInteractome implements Master<Gene> {

	private static final Logger log = Logger.getLogger(DbSpecies.class);

	private Counter<Gene> counter = null;

	private DatabaseManager databaseManager;

	private final int species_id;

	protected DbSpecies(DatabaseManager databaseManager, String species,
			int species_id) {
		super(species, 1, 0.0, Type.Species, true);
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
		counter = new Counter<Gene>();
		long identifier = accession.getIdentifier();

		databaseManager.pullOrthologs(counter, identifier, this);

		if (counter.getTotal() == 0) {
			ubergraph.newGene(accession).setMembership(this, 1);
		} else {
			if (counter.size() > 1) {
				/* Attempt to merge duplicate entries. */
				CliqueMerger<Gene> merger = new CliqueMerger<Gene>(counter
						.set(), this);
				merger.merge();
			}

			if (counter.size() > 1) {
				StringBuilder sb = new StringBuilder();
				sb.append("Splitting accession ");
				accession.show(sb);
				sb.append(" among");
				for (Entry<Gene, Integer> entry : counter)
					entry.getKey().show(sb.append(" "));
				log.warn(sb);
			}
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

	public boolean merge(Gene gene, Gene victim) {
		counter.transfer(victim, gene);
		return Ubergraph.getInstance().merge(gene, victim);
	}

	public boolean prepare() {
		if (databaseManager == null) {
			return true;
		}
		log.info("Loading species " + name);
		try {
			for (Accession accession : databaseManager.pullAccessions(this)) {
				addToGenes(accession);
			}

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
