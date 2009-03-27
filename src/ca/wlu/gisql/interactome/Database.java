package ca.wlu.gisql.interactome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.wlu.gisql.DatabaseManager;
import ca.wlu.gisql.interation.Interaction;
import ca.wlu.gisql.interation.NebulonInteraction;

public class Database extends Interactome {
	static final Logger log = Logger.getLogger(Database.class);

	private Connection conn;

	private Map<Long, Long> orthologs = new HashMap<Long, Long>();

	private String species;

	private int species_id;

	public Database(String species, int species_id, Connection conn) {
		this.species = species;
		this.species_id = species_id;
		this.conn = conn;
	}

	public long findOrtholog(long gene) {
		Long ortholog = orthologs.get(gene);
		if (ortholog != null) {
			return ortholog;
		}

		try {
			PreparedStatement orthologStatement = conn
					.prepareStatement("SELECT query_gene FROM ortholog WHERE match_gene = ? UNION SELECT match_gene FROM ortholog WHERE query_gene = ?");
			orthologStatement.setInt(1, species_id);
			orthologStatement.setInt(2, species_id);
			ortholog = (Long) DatabaseManager.executeScalar(orthologStatement);
			if (ortholog == null) {

				PreparedStatement selfStatement = conn
						.prepareStatement("SELECT COUNT(id) FROM gene WHERE species = ? AND id = ?");
				selfStatement.setInt(1, species_id);
				selfStatement.setLong(2, gene);
				long self = (Long) DatabaseManager.executeScalar(selfStatement);
				if (self == 0)
					ortholog = -1L;
				else {
					ortholog = gene;
				}
			}
			orthologs.put(gene, ortholog);
			return ortholog;

		} catch (SQLException e) {
			log.error("SQL exception trying to match ortholog for " + gene, e);
			return -1;
		}
	}

	protected void prepareInteractions() {
		log.info("Loading species " + species);
		try {
			PreparedStatement interactionStatement = conn
					.prepareStatement("SELECT gene1, gene2, score FROM interaction JOIN gene g1 ON gene1 = g1.id JOIN gene g2 ON gene2 = g2.id WHERE g1.species = ? AND g2.species = ?");
			interactionStatement.setInt(1, species_id);
			interactionStatement.setInt(2, species_id);
			ResultSet rs = interactionStatement.executeQuery();
			while (rs.next()) {
				long gene1 = Math.min(rs.getLong(1), rs.getLong(2));
				long gene2 = Math.max(rs.getLong(1), rs.getLong(2));
				double membership = rs.getDouble(3);
				Interaction i = new NebulonInteraction(this, gene1, gene2,
						membership);
				addInteraction(i);
			}
			log.info("Load complete.");
		} catch (SQLException e) {
			log.fatal("Failed to load species " + species, e);
		}
	}

	public StringBuilder show(StringBuilder sb) {
		return sb.append(species);
	}

}