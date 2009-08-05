package ca.wlu.gisql.interactome.cut;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Cut implements Interactome {
	public final static Parseable descriptor = new CutDescriptor();

	private final double cutoff;

	private final Interactome interactome;

	public Cut(Interactome interactome, double cutoff) {
		super();
		this.interactome = interactome;
		this.cutoff = cutoff;
	}

	public double calculateMembership(Gene gene) {
		double membership = interactome.calculateMembership(gene);
		if (GisQL.isMissing(membership) || membership < cutoff)
			return GisQL.Missing;
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interactome.calculateMembership(interaction);
		if (GisQL.isMissing(membership) || membership < cutoff)
			return GisQL.Missing;
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return interactome.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
	}

	public double membershipOfUnknown() {
		return interactome.membershipOfUnknown();
	}

	public boolean postpare() {
		return interactome.postpare();
	}

	public boolean prepare() {
		return interactome.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(interactome, this.getPrecedence());
		print.print(" [");
		print.print(cutoff);
		print.print("]");

	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
