package ca.wlu.gisql.interactome;

import java.util.Set;

import org.jgrapht.graph.SimpleWeightedGraph;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Generate jgrapht objects from an interactome. */
public class GraphedInteractome implements Interactome {
	private final SimpleWeightedGraph<Gene, Interaction> graph = new SimpleWeightedGraph<Gene, Interaction>(
			Interaction.class);

	private final Interactome source;

	public GraphedInteractome(Interactome source) {
		super();
		this.source = source;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!Membership.isMissing(membership)) {
			graph.addVertex(interaction.getGene1());
			graph.addVertex(interaction.getGene2());
			graph.addEdge(interaction.getGene1(), interaction.getGene2(),
					interaction);
		}
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public SimpleWeightedGraph<Gene, Interaction> getGraph() {
		return graph;
	}

	public int getPrecedence() {
		return source.getPrecedence();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
	}

}
