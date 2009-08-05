package ca.wlu.gisql.interactome.proximity;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Proximity implements Interactome {

	public final static Parseable descriptor = new ProximityDescriptor();

	private static final Logger log = Logger.getLogger(Proximity.class);
	private final Set<Long> accessions;
	private Set<Interactome> context;
	private final SimpleWeightedGraph<Gene, Interaction> graph;
	private final int radius;

	private final Interactome source;

	public Proximity(Interactome source,
			SimpleWeightedGraph<Gene, Interaction> graph, int radius,
			Set<Long> accessions) {
		this.source = source;
		this.graph = graph;
		this.radius = radius;
		this.accessions = accessions;
		this.context = GisQL.collectAll(this);
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (GisQL.isPresent(membership) && graph.containsVertex(gene)) {
			for (long accession : accessions) {
				for (Gene destination : Ubergraph.getInstance().findGenes(
						accession)) {
					if (source == destination) {
						gene.setMembership(this, membership);
						return membership;
					}
					if (graph.containsVertex(destination)) {
						ShowableStringBuilder<Set<Interactome>> print = new ShowableStringBuilder<Set<Interactome>>(
								context);
						print.print("Searching for path between ");
						print.print(gene);
						print.print(" and ");
						print.print(destination);
						log.error(print);
						DijkstraShortestPath<Gene, Interaction> dijkstra = new DijkstraShortestPath<Gene, Interaction>(
								graph, gene, destination);
						List<Interaction> path = dijkstra.getPathEdgeList();
						if (path != null && path.size() <= radius) {
							log.warn("There is a path of length = "
									+ path.size() + " and weight = "
									+ dijkstra.getPathLength());
							gene.setMembership(this, membership);
							return membership;
						}
					}
				}
			}
		}
		gene.setMembership(this, GisQL.Missing);
		return GisQL.Missing;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (GisQL.isPresent(interaction.getGene1().getMembership(this))
				&& GisQL.isPresent(interaction.getGene2().getMembership(this))) {
			return membership;
		} else {
			return GisQL.Missing;
		}
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		boolean result = source.prepare();
		log
				.info("We are going to have a run Dijkstra's algorithm N times on a graph of N nodes where N = "
						+ graph.edgeSet().size());
		return result;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(":near (");
		boolean first = true;
		for (long accession : accessions) {
			if (first) {
				first = false;
			} else {
				print.print(", ");
			}
			print.print(accession);
		}
		print.print(")");
		if (radius < Integer.MAX_VALUE) {
			print.print(' ');
			print.print(radius);
		}
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
