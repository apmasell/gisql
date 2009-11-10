package ca.wlu.gisql.interactome.snap;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

@GisqlConstructorFunction(name = "snap", description = "Filter out interactions where the genes are not present.")
public class Snap implements Interactome {

	private final Interactome source;

	public Snap(Interactome source) {
		super();
		this.source = source;
	}

	@Override
	public double calculateMembership(Gene gene) {
		double membership = gene.getMembership(this);
		if (Membership.isUndefined(membership)) {
			membership = source.calculateMembership(gene);
			gene.setMembership(this, membership);
		}
		return membership;
	}

	@Override
	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!Membership.isMissing(membership)
				&& !Membership.isMissing(interaction.getGene1().getMembership(
						this))
				&& !Membership.isMissing(interaction.getGene2().getMembership(
						this))) {
			return membership;
		} else {
			return Membership.Missing;
		}
	}

	@Override
	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	@Override
	public Construction getConstruction() {
		return Construction.Computed;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	@Override
	public boolean postpare() {
		return source.postpare();
	}

	@Override
	public boolean prepare() {
		return source.prepare();
	}

	@Override
	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
		print.print(": snap");
	}

}
