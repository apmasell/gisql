package ca.wlu.gisql.interactome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interaction.Interaction;
import ca.wlu.gisql.interaction.NebulonInteraction;

public class Database extends AbstractInteractome {
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
	if (ortholog == null) {
	    return -1L;
	} else {
	    return ortholog;
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
	    rs.close();

	    for (String query : new String[] {
		    "SELECT id, id FROM gene WHERE species = ?",
		    "SELECT match_gene, query_gene FROM ortholog JOIN gene ON match_gene = id WHERE species = ?",
		    "SELECT query_gene, match_gene FROM ortholog JOIN gene ON query_gene = id WHERE species = ?" }) {
		PreparedStatement orthologStatement = conn
			.prepareStatement(query);
		orthologStatement.setInt(1, species_id);
		rs = orthologStatement.executeQuery();
		while (rs.next()) {
		    long localgene = rs.getLong(1);
		    long remotegene = rs.getLong(2);
		    orthologs.put(remotegene, localgene);
		}
		rs.close();
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