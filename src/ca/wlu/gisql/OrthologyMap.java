package ca.wlu.gisql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class OrthologyMap {
    static final Logger log = Logger.getLogger(OrthologyMap.class);

    private Connection conn;

    private Map<Long, Map<Integer, Long>> data = new HashMap<Long, Map<Integer, Long>>();

    private Set<Integer> knownSpecies = new HashSet<Integer>();

    public OrthologyMap(Connection conn) {
	this.conn = conn;
    }

    public void add(int species) {
	try {
	    if (knownSpecies.contains(species))
		return;
	    PreparedStatement self = conn
		    .prepareStatement("SELECT match_gene, mg.species, query_gene, qg.species FROM ortholog JOIN gene mg ON match_gene = mg.id JOIN gene qg ON query_gene = qg.id WHERE mg.species = ? AND qg.species = ?");
	    self.setInt(1, species);
	    self.setInt(2, species);
	    addFromDb(self);

	    for (int remotespecies : knownSpecies) {
		for (String query : new String[] {
			"SELECT match_gene, mg.species, query_gene, qg.species FROM ortholog JOIN gene mg ON match_gene = mg.id JOIN gene qg ON query_gene = qg.id WHERE mg.species = ? AND qg.species = ?",
			"SELECT match_gene, mg.species, query_gene, qg.species FROM ortholog JOIN gene mg ON match_gene = mg.id JOIN gene qg ON query_gene = qg.id WHERE qg.species = ? AND mg.species = ?" }) {

		    PreparedStatement statement = conn.prepareStatement(query);
		    statement.setInt(1, species);
		    statement.setInt(2, remotespecies);
		    addFromDb(statement);
		}
	    }
	    knownSpecies.add(species);
	} catch (SQLException e) {
	    log.error("Failed processing query.", e);
	}
    }

    private void add(long gene, int species, long ortholog) {
	Map<Integer, Long> submap = data.get(gene);
	if (submap == null) {
	    submap = new HashMap<Integer, Long>();
	    data.put(gene, submap);
	}
	submap.put(species, ortholog);
    }

    private void addFromDb(PreparedStatement statement) throws SQLException {
	ResultSet rs = statement.executeQuery();
	while (rs.next()) {
	    long gene = rs.getLong(1);
	    int species = rs.getInt(2);
	    long ortholog = rs.getLong(3);
	    int orthospecies = rs.getInt(4);
	    add(gene, orthospecies, ortholog);
	    add(ortholog, species, gene);
	}
	rs.close();
    }

    public Long findOrtholog(int species, long gene) {
	Map<Integer, Long> submap = data.get(gene);
	if (submap == null)
	    return null;
	return submap.get(species);
    }
}
