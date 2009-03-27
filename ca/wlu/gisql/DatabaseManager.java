package ca.wlu.gisql;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.wlu.gisql.interactome.Database;

public class DatabaseManager {
	Connection conn;

	static final Logger log = Logger.getLogger(DatabaseManager.class);

	private Map<String, Database> loadedSpecies = new HashMap<String, Database>();

	public DatabaseManager() throws SQLException, ClassNotFoundException,
			IOException {
		Class.forName("org.postgresql.Driver");
		Properties props = new Properties();
		URL url = ClassLoader.getSystemResource("gisql.properties");
		props.load(url.openStream());

		conn = DriverManager.getConnection("jdbc:postgresql:"
				+ props.getProperty("url"), props);
	}

	public Database getSpeciesInteractome(String species) {
		try {
			Database s = loadedSpecies.get(species);
			if (s == null) {
				PreparedStatement idStatement = conn
						.prepareStatement("SELECT id FROM species WHERE name = ?");
				idStatement.setString(1, species);
				int species_id = (Integer) DatabaseManager
						.executeScalar(idStatement);
				log.info("Species " + species + " has id " + species_id);

				s = new Database(species, species_id, conn);
				loadedSpecies.put(species, s);
			}
			return s;
		} catch (SQLException e) {
			log.error("Database error fetching species " + species, e);
			return null;
		}
	}

	public static Object executeScalar(PreparedStatement statement)
			throws SQLException {
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			return rs.getObject(1);
		} else {
			return null;
		}
	}
}
