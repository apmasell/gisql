package ca.wlu.gisql.interactome.proximity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

@GisqlConstructorFunction(name = "near", description = "Find genes with in a specified number of degrees from genes specified by a list of gi numbers. Use iinf for maximum radius.")
public class Proximity implements Interactome {

	private final Set<Gene> connectedGenes = new HashSet<Gene>();

	private final Set<Gene> genes;

	private final long radius;

	private final Interactome source;

	public Proximity(Interactome source, Long radius, List<Gene> genes) {
		this.source = source;
		this.radius = radius;
		this.genes = new HashSet<Gene>(genes);
	}

	public double calculateMembership(Gene gene) {
		if (connectedGenes.contains(gene)) {
			return source.calculateMembership(gene);
		} else {
			return Membership.Missing;
		}
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (connectedGenes.contains(interaction.getGene1())
				&& connectedGenes.contains(interaction.getGene2())) {
			return membership;
		} else {
			return Membership.Missing;
		}
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return Construction.Computed;
	}

	public int getPrecedence() {
		return Parser.PREC_UNARY_MANGLE;
	}

	public double membershipOfUnknown() {
		return 0;
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		boolean result = source.prepare();
		if (result) {
			Queue<Gene> queue = new LinkedList<Gene>();
			queue.addAll(genes);
			/* Null acts as a sentinal when we complete a level in the tree. */
			queue.add(null);

			int depth = 0;
			while (depth <= radius && queue.size() > 1) {
				Gene current = queue.poll();
				if (current == null) {
					depth++;
					queue.add(null);
				} else {
					for (Interaction interaction : current.getInteractions()) {
						Gene other = interaction.getOther(current);
						if (Membership.isPresent(source
								.calculateMembership(interaction))
								&& !connectedGenes.contains(other)
								&& !queue.contains(other)) {
							queue.add(other);
						}
					}
					connectedGenes.add(current);
				}
			}
		}
		return result;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(":near ");
		print.print(genes);
		print.print(" ");
		if (radius < Long.MAX_VALUE) {
			print.print(radius);
		} else {
			print.print("iinf");
		}
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
