package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import ca.wlu.gisql.OrthologyMap;
import ca.wlu.gisql.gene.DbGene;
import ca.wlu.gisql.gene.Gene;
import ca.wlu.gisql.interaction.DbInteraction;
import ca.wlu.gisql.interaction.Interaction;

public class DbSpecies extends AbstractInteractome {
	static final Logger log = Logger.getLogger(DbSpecies.class);

	private Connection conn;

	private OrthologyMap orthologs;

	private String species;

	private int species_id;

	public DbSpecies(OrthologyMap orthologs, String species, int species_id,
			Connection conn) {
		this.orthologs = orthologs;
		this.species = species;
		this.species_id = species_id;
		this.conn = conn;
	}

	public int countOrthologs(Gene gene) {
		return (this.findOrtholog(gene) == null ? 0 : 1);
	}

	public Gene findOrtholog(Gene gene) {
		return findRootOrtholog(gene);
	}

	public Gene findRootOrtholog(Gene gene) {
		Long geneid = orthologs.findOrtholog(species_id, gene.getId());
		if (geneid == null)
			return null;
		return getGene(geneid);
	}

	protected double membershipOfUnknown() {
		return 0;
	}

	public int numGenomes() {
		return 1;
	}

	protected void prepareInteractions() {
		log.info("Loading species " + species);
		ResultSet rs;
		try {
			PreparedStatement geneStatement = conn
					.prepareStatement("SELECT id, name FROM gene WHERE species = ?");
			geneStatement.setInt(1, species_id);
			rs = geneStatement.executeQuery();
			while (rs.next()) {
				addGene(new DbGene(this, rs.getLong(1), rs.getString(2)));
			}
			rs.close();

			PreparedStatement interactionStatement = conn
					.prepareStatement("SELECT gene1, gene2, score FROM interaction JOIN gene g1 ON gene1 = g1.id JOIN gene g2 ON gene2 = g2.id WHERE g1.species = ? AND g2.species = ?");
			interactionStatement.setInt(1, species_id);
			interactionStatement.setInt(2, species_id);
			rs = interactionStatement.executeQuery();
			while (rs.next()) {
				long gene1 = Math.min(rs.getLong(1), rs.getLong(2));
				long gene2 = Math.max(rs.getLong(1), rs.getLong(2));
				double membership = rs.getDouble(3);
				Interaction i = new DbInteraction(this, getGene(gene1),
						getGene(gene2), membership);
				addInteraction(i);
			}
			rs.close();

			orthologs.add(species_id);
			log.info("Load complete.");
		} catch (SQLException e) {
			log.fatal("Failed to load species " + species, e);
		}
	}

	public PrintStream show(PrintStream print) {
		print.print(species);
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		return sb.append(species);
	}
}
