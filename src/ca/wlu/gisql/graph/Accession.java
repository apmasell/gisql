package ca.wlu.gisql.graph;

public final class Accession {
	private final long identifier;

	private final String name;

	private final int species;

	public Accession(long identifier, int species, String name) {
		this.identifier = identifier;
		this.species = species;
		this.name = name;
	}

	public long getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public int getSpecies() {
		return species;
	}

}