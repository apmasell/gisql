package ca.wlu.gisql.interactome.patch;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Patch implements Interactome {
	public final static Parseable descriptor = new PatchDescriptor();

	private final Double membership;
	private final Interactome source;

	public Patch(Interactome source, Double membership) {
		this.source = source;
		this.membership = membership;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (GisQL.isMissing(membership)
				&& !GisQL.isMissing(source.calculateMembership(interaction
						.getGene1()))
				&& !GisQL.isMissing(source.calculateMembership(interaction
						.getGene2()))) {
			if (this.membership == null) {
				return interaction.getAverageMembership();
			} else {
				return this.membership;
			}
		} else {
			return membership;
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
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, this.getPrecedence());
		print.print(" $");
		if (membership != null) {
			print.print(" ");
			print.print(membership);
		}
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
