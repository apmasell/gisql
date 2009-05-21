package ca.wlu.gisql.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

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
			if (counter.size() > 1) {
				/* Attempt to merge duplicate entries. */
				SimpleGraph<Gene, DefaultEdge> compatibility = new SimpleGraph<Gene, DefaultEdge>(
						DefaultEdge.class);
				List<Gene> genes = new ArrayList<Gene>(counter.set());
				for (int i = 0; i < genes.size(); i++) {
					for (int j = i + 1; j < genes.size(); j++) {
						if (ubergraph.canMerge(genes.get(i), genes.get(j))) {
							compatibility.addEdge(genes.get(i), genes.get(j));
						}
					}
				}

				BronKerboschCliqueFinder<Gene, DefaultEdge> cliques = new BronKerboschCliqueFinder<Gene, DefaultEdge>(
						compatibility);
				for (Set<Gene> mergeable : cliques.getAllMaximalCliques()) {
					if (mergeable.size() < 2)
						break;
					Iterator<Gene> geneIterator = mergeable.iterator();
					Gene gene = geneIterator.next();
					while (geneIterator.hasNext()) {
						Gene victim = geneIterator.next();
						if (!ubergraph.merge(gene, victim))
							throw new RuntimeException(
									"Gene merging failed unexpectedly.");
						counter.transfer(victim, gene);
					}
				}
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
