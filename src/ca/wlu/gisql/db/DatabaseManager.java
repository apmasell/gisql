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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.graph.Accession;
import ca.wlu.gisql.graph.BiologicalFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.graph.biologicalfunctions.Cog;
import ca.wlu.gisql.graph.biologicalfunctions.GeneOntology;
import ca.wlu.gisql.interactome.Interactome;

public class DatabaseManager {

	private static final Logger log = Logger.getLogger(DatabaseManager.class);

	private static final Map<Integer, Gene> orthogroups = new HashMap<Integer, Gene>();

	public static Properties getPropertiesFromFile() throws IOException {
		Properties properties = new Properties();
		String filename = System.getProperty("gisql.properties");
		InputStream is = new FileInputStream(filename == null
				|| filename.length() == 0 ? "gisql.properties" : filename);
		if (is == null) {
			log.fatal("Cannot find gisql.properties.");
			throw new RuntimeException("Failed to connect to database.");
		}
		properties.load(is);
		return properties;
	}

	private final Set<DbSpecies> all = new HashSet<DbSpecies>();

	private final Connection connection;

	public DatabaseManager(Properties properties) throws SQLException,
			ClassNotFoundException {
		Class.forName("org.postgresql.Driver");

		log.info("Connecting to database.");
		connection = DriverManager.getConnection(properties.getProperty("url"),
				properties);
	}

	public Set<DbSpecies> getSpecies() {

		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT id, name FROM species");
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int species_id = rs.getInt(1);
				String species = rs.getString(2);
				DbSpecies interactome = new DbSpecies(this, species, species_id);
				all.add(interactome);
			}
			return all;
		} catch (SQLException e) {
			log.error("Database error fetching species.", e);
			return null;
		}
	}

	protected void populateArrays(DatabaseEnvironment environment,
			Map<Integer, Interactome> speciesById) {
		try {
			PreparedStatement arrayStatement = connection
					.prepareStatement("SELECT id, name FROM userarray");
			ResultSet arrayrs = arrayStatement.executeQuery();
			while (arrayrs.next()) {
				int arrayid = arrayrs.getInt(1);
				String name = arrayrs.getString(2);
				List<Interactome> list = new ArrayList<Interactome>();
				try {
					PreparedStatement membersStatement = connection
							.prepareStatement("SELECT species FROM arraymembers WHERE userarray = ?");
					membersStatement.setInt(1, arrayid);
					ResultSet memberrs = membersStatement.executeQuery();
					while (memberrs.next()) {
						Interactome interactome = speciesById.get(memberrs
								.getInt(1));
						if (interactome != null) {
							list.add(interactome);
						}
					}
					memberrs.close();
					environment.putArray(name, list);
				} catch (SQLException e) {
					log.error("Error processing array " + name, e);
				}

			}
			arrayrs.close();
		} catch (SQLException e) {
			log.error("Error processing arrays. ", e);
		}
	}

	Set<BiologicalFunction> pullFunctions(Accession accession)
			throws SQLException {
		Set<BiologicalFunction> functions = new HashSet<BiologicalFunction>();
		PreparedStatement interactionStatement = connection
				.prepareStatement("SELECT function FROM cog WHERE gene = ?");
		interactionStatement.setLong(1, accession.getIdentifier());
		ResultSet rs = interactionStatement.executeQuery();
		while (rs.next()) {
			String s = rs.getString(1);
			for (Character id : s.toCharArray()) {
				Cog cog = Cog.makeCog(id);
				functions.add(cog);
			}
		}
		rs.close();

		interactionStatement = connection
				.prepareStatement("SELECT term FROM go WHERE gene = ?");
		interactionStatement.setLong(1, accession.getIdentifier());
		rs = interactionStatement.executeQuery();
		while (rs.next()) {
			int term = rs.getInt(1);
			functions.add(GeneOntology.makeGO(term));
		}
		rs.close();

		return functions;
	}

	void pullGenes(DbSpecies species) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT id, name, ogrp FROM gene WHERE species = ?");
		statement.setInt(1, species.getId());
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			Accession accession = new Accession(rs.getLong(1), species, rs
					.getString(2));
			int orthogroup = rs.getInt(3);

			Gene gene;
			if (orthogroup == 0 || !orthogroups.containsKey(orthogroup)) {
				gene = Ubergraph.getInstance().newGene(accession);
			} else {
				gene = Ubergraph.getInstance().addOrtholog(
						orthogroups.get(orthogroup), accession);
			}
			gene.setMembership(species, 1);
			if (orthogroup != 0) {
				orthogroups.put(orthogroup, gene);
			}
			for (BiologicalFunction function : pullFunctions(accession)) {
				gene.add(function, species, 1);
			}
		}
		rs.close();
	}

	void pullInteractions(DbSpecies interactome) throws SQLException {
		PreparedStatement interactionStatement = connection
				.prepareStatement("SELECT gene1, gene2, score FROM interaction JOIN gene g1 ON gene1 = g1.id JOIN gene g2 ON gene2 = g2.id WHERE gene1 != gene2 AND g1.species = g2.species AND g1.species = ?");
		interactionStatement.setInt(1, interactome.getId());
		ResultSet rs = interactionStatement.executeQuery();
		while (rs.next()) {
			long identifier1 = Math.min(rs.getLong(1), rs.getLong(2));
			long identifier2 = Math.max(rs.getLong(1), rs.getLong(2));
			double membership = rs.getDouble(3);
			interactome.addInteraction(identifier1, identifier2, membership);
		}
		rs.close();
	}
}
