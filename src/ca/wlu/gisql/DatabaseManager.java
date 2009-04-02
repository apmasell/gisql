package ca.wlu.gisql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    Connection conn;

    private Map<String, Database> loadedSpecies = new HashMap<String, Database>();

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
		Integer species_id = (Integer) DatabaseManager
			.executeScalar(idStatement);
		if (species_id == null) {
		    log.error("Unknown species " + species);
		    return null;
		}
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

    public String getSpeciesName(int index) {
	for (String name : loadedSpecies.keySet()) {
	    if (index == 0) {
		return name;
	    }
	    index--;
	}
	return null;
    }

    public int sizeSpecies() {
	return loadedSpecies.size();
    }
}
