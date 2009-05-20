package ca.wlu.gisql.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.util.Counter;

public class DatabaseManager {

	static final Logger log = Logger.getLogger(DatabaseManager.class);

	public static Object executeScalar(PreparedStatement statement)
			throws SQLException {
		Object result = null;
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			result = rs.getObject(1);
		}
		rs.close();
		return result;

	}

	private Connection connection;

	public DatabaseManager() throws SQLException, ClassNotFoundException,
			IOException {
		Class.forName("org.postgresql.Driver");
		Properties props = new Properties();
		InputStream is = new FileInputStream("gisql.properties");
		if (is == null) {
			log.fatal("Cannot find gisql.properties.");
			throw new RuntimeException("Failed to connect to database.");
		}
		props.load(is);

		log.info("Connecting to database.");
		connection = DriverManager.getConnection("jdbc:postgresql:"
				+ props.getProperty("url"), props);
	}

	public List<DbSpecies> getSpecies() {

		try {
			List<DbSpecies> list = new ArrayList<DbSpecies>();
			PreparedStatement statement = connection
					.prepareStatement("SELECT id, name FROM species");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int species_id = rs.getInt(1);
				String species = rs.getString(2);
				DbSpecies interactome = new DbSpecies(this, species, species_id);
				list.add(interactome);
			}
			return list;
		} catch (SQLException e) {
			log.error("Database error fetching species.", e);
			return null;
		}
	}

	List<Accession> pullAccessions(int species_id) throws SQLException {
		List<Accession> list = new ArrayList<Accession>();
		PreparedStatement statement = connection
				.prepareStatement("SELECT id, name FROM gene WHERE species = ?");
		statement.setInt(1, species_id);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			list.add(new Accession(rs.getLong(1), species_id, rs.getString(2)));
		}
		rs.close();

		return list;
	}

	void pullInteractions(DbSpecies interactome) throws SQLException {
		PreparedStatement interactionStatement = connection
				.prepareStatement("SELECT gene1, gene2, score FROM interaction JOIN gene g1 ON gene1 = g1.id JOIN gene g2 ON gene2 = g2.id WHERE gene1 != gene2 AND g1.species = ? AND g2.species = ?");
		interactionStatement.setInt(1, interactome.getId());
		interactionStatement.setInt(2, interactome.getId());
		ResultSet rs = interactionStatement.executeQuery();
		while (rs.next()) {
			long identifier1 = Math.min(rs.getLong(1), rs.getLong(2));
			long identifier2 = Math.max(rs.getLong(1), rs.getLong(2));
			double membership = rs.getDouble(3);
			interactome.addInteraction(identifier1, identifier2, membership);
		}
		rs.close();
	}

	void pullOrthologs(Counter<Gene> counter, long identifier, int species_id)
			throws SQLException {
		for (String query : new String[] {
				"SELECT match_gene FROM ortholog WHERE query_gene = ?",
				"SELECT query_gene FROM ortholog WHERE match_gene = ?" }) {

			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, identifier);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				long ortholog = rs.getLong(1);
				if (ortholog != identifier) {
					for (Gene match : Ubergraph.getInstance().findGenes(
							ortholog)) {
						boolean add = true;
						for (Accession accession : match) {
							if (accession.getSpecies() == species_id) {
								add = false;
								break;
							}
						}
						if (add)
							counter.add(match);
					}
				}
			}
			rs.close();
		}
	}
}
