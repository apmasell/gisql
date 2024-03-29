package ca.wlu.gisql.graph;

import java.util.Set;

import ca.wlu.gisql.db.DbSpecies;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Show;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

/**
 * Representation of a single gene, identified by a gi, in a single species.
 * This matches NCBI's vision of a gene.
 */
public final class Accession implements Show<Set<Interactome>> {
	private final Set<String> altnames;

	private final long identifier;

	private final String name;

	private final DbSpecies species;

	public Accession(long identifier, DbSpecies species, String name,
			Set<String> altnames) {
		this.identifier = identifier;
		this.species = species;
		this.name = name;
		this.altnames = altnames;
	}

	public Set<String> getAlternativeNames() {
		return altnames;
	}

	public long getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public DbSpecies getSpecies() {
		return species;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(name);
		print.print("[gi:");
		print.print(identifier);
		print.print("/");
		print.print(species);
		print.print("]");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder.toString(this, null);
	}
}