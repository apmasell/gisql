package ca.wlu.gisql.interactome.cut;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

@GisqlConstructorFunction(name = "cut", description = "Filter interactions and genes with a score lower than threshold")
public class Cut implements Interactome {
	public static final Parseable descriptor = new CutDescriptor();

	private final double cutoff;

	private final Interactome interactome;

	public Cut(Interactome interactome,
			@GisqlType(type = "membership") double cutoff) {
		super();
		this.interactome = interactome;
		this.cutoff = cutoff;
	}

	public double calculateMembership(Gene gene) {
		double membership = interactome.calculateMembership(gene);
		if (Membership.isMissing(membership) || membership < cutoff) {
			return Membership.Missing;
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interactome.calculateMembership(interaction);
		if (Membership.isMissing(membership) || membership < cutoff) {
			return Membership.Missing;
		}
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return interactome.collectAll(set);
	}

	public Construction getConstruction() {
		return Construction.Computed;
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
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
		print.print(interactome, getPrecedence());
		print.print(" {");
		print.print(cutoff);
		print.print("}");

	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
