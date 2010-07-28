package ca.wlu.gisql.interactome.output;

public enum FileFormat {
	adjacency("MatLab Adjacency Matrix"), dot("Dot Graph"), genetbl(
			"Genome Table"), gml("GML File"), graphml("GraphML File"), interactiontbl(
			"Interactome Table"), laplace("MatLab Laplace Matrix"), summary(
			"Summary Statistics Only");
	private final String description;

	private FileFormat(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

}