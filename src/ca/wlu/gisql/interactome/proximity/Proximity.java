package ca.wlu.gisql.interactome.proximity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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

	private final Set<Long> accessions;

	private Set<Gene> connectedGenes = new HashSet<Gene>();

	private final int radius;

	private final Interactome source;

	public Proximity(Interactome source, int radius, Set<Long> accessions) {
		this.source = source;
		this.radius = radius;
		this.accessions = accessions;
	}

	public double calculateMembership(Gene gene) {
		if (connectedGenes.contains(gene)) {
			return source.calculateMembership(gene);
		} else {
			return GisQL.Missing;
		}
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (connectedGenes.contains(interaction.getGene1())
				&& connectedGenes.contains(interaction.getGene2())) {
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
		if (result) {
			Queue<Gene> queue = new LinkedList<Gene>();
			for (long accession : accessions) {
				queue.addAll(Ubergraph.getInstance().findGenes(accession));
			}
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
						if (GisQL.isPresent(source
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
