package ca.wlu.gisql.interactome;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.gui.output.GeneTable;
import ca.wlu.gisql.gui.output.InteractionTable;

public class CachedInteractome implements Interactome {

	private static final Logger log = Logger.getLogger(CachedInteractome.class);

	public static CachedInteractome wrap(Interactome interactome, String name) {
		if (interactome == null)
			return null;

		if (interactome instanceof CachedInteractome) {
			CachedInteractome cachedInteractome = (CachedInteractome) interactome;
			if (name != null && cachedInteractome.name == null) {
				log.info("Renaming from " + cachedInteractome.name + " to "
						+ name);
				cachedInteractome.name = name;
			}
			return cachedInteractome;
		}

		return new CachedInteractome(interactome, name, 0, 1);
	}

	private boolean first = true;

	private ListOrderedSet<Gene> genes;

	private final GeneTable geneTable;

	private final SimpleWeightedGraph<Gene, Interaction> graph = new SimpleWeightedGraph<Gene, Interaction>(
			Interaction.class);

	private ListOrderedSet<Interaction> interactions;

	private final InteractionTable interactionTable;

	protected final double lowerbound;

	private String name;

	protected final Interactome source;

	protected final double upperbound;

	public CachedInteractome(Interactome source, String name,
			double lowerbound, double upperbound) {
		super();
		this.source = source;
		geneTable = new GeneTable(this);
		interactionTable = new InteractionTable(this);
		this.name = name;
		this.lowerbound = lowerbound;
		this.upperbound = upperbound;
	}

	public double calculateMembership(Gene gene) {
		if (first) {
			double membership = source.calculateMembership(gene);
			if (membership >= lowerbound && membership <= upperbound) {
				gene.setMembership(this, membership);
				genes.add(gene);
			}
			return membership;
		} else {
			return gene.getMembership(this);
		}
	}

	public double calculateMembership(Interaction interaction) {
		if (first) {
			double membership = source.calculateMembership(interaction);
			if (membership >= lowerbound && membership <= upperbound) {
				interaction.setMembership(this, membership);
				interactions.add(interaction);
				for (Gene gene : new Gene[] { interaction.getGene1(),
						interaction.getGene2() }) {
					if (GisQL.isMissing(gene.getMembership(this))) {
						gene.setMembership(this, 0);
						genes.add(gene);
					}
				}
				graph.addVertex(interaction.getGene1());
				graph.addVertex(interaction.getGene2());
				graph.addEdge(interaction.getGene1(), interaction.getGene2(),
						interaction);
			}
			return membership;
		} else {
			return interaction.getMembership(this);
		}
	}

	public final List<Gene> getGenes() {
		process();
		return genes.asList();
	}

	public final GeneTable getGeneTable() {
		process();
		return geneTable;
	}

	public final UndirectedGraph<Gene, Interaction> getGraph() {
		process();
		return graph;
	}

	public final List<Interaction> getInteractions() {
		process();
		return interactions.asList();
	}

	public final InteractionTable getInteractionTable() {
		return interactionTable;
	}

	protected final String getName() {
		return name;
	}

	public int getPrecedence() {
		return source.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
	}

	public final double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public final int numGenomes() {
		return source.numGenomes();
	}

	public boolean postpare() {
		first = false;
		return source.postpare();
	}

	public boolean prepare() {
		genes = new ListOrderedSet<Gene>();
		interactions = new ListOrderedSet<Interaction>();
		return source.prepare();
	}

	public final boolean process() {
		if (first) {
			if (!prepare()) {
				log.error("Preparation failed.");
				return false;
			}
			for (Gene gene : Ubergraph.getInstance().genes()) {
				this.calculateMembership(gene);
			}
			for (Interaction interaction : Ubergraph.getInstance()) {
				this.calculateMembership(interaction);
			}
			if (!postpare()) {
				log.error("Postparation failed.");
				return false;
			}
		}
		return true;

	}

	public PrintStream show(PrintStream print) {
		return source.show(print);
	}

	public StringBuilder show(StringBuilder sb) {
		return source.show(sb);
	}

	public String toString() {
		return (name == null ? "<unknown>" : name);
	}
}
