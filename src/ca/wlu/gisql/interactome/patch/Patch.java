package ca.wlu.gisql.interactome.patch;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Patch implements Interactome {
	public static final Function function = new PatchFunction();
	public static final Function functionAverage = new AveragePatchFunction();

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
		if (Membership.isMissing(membership)
				&& !Membership.isMissing(source.calculateMembership(interaction
						.getGene1()))
				&& !Membership.isMissing(source.calculateMembership(interaction
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

	public Construction getConstruction() {
		return Construction.Computed;
	}

	public int getPrecedence() {
		return function.getPrecedence();
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
		print.print(source, getPrecedence());
		print.print(" :blanks");
		if (membership != null) {
			print.print(" ");
			print.print(membership);
		}
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
