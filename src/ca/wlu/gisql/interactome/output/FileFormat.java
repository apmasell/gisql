package ca.wlu.gisql.interactome.output;

public enum FileFormat {
	adjacency("MatLab Adjacency Matrix"), dot("Dot Graph"), genome(
			"Genome Table"), gml("GML File"), graphml("GraphML File"), interactome(
			"Interactome Table"), laplace("MatLab Laplace Matrix"), summary(
			"Summary Statistics Only");
	private final String description;

	private FileFormat(String description) {
		this.description = description;
	}

	public String toString() {
		return description;
	}

}