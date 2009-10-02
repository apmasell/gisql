package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

public class Coreicity implements Interactome {

	public static final Function function = new CoreicityDescriptor();
	private final Program comparison;
	private final Machine machine;
	private final Interactome source;

	public Coreicity(Interactome source, Machine machine, Program comparison) {
		this.source = source;
		this.machine = machine;
		this.comparison = comparison;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (!Membership.isMissing(membership)
				&& !(Boolean) comparison.run(machine, (long) gene
						.getCoreicity())) {
			membership = 0;
		}
		gene.setMembership(this, membership);
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!Membership.isMissing(membership)
				&& (Membership.isPresent(interaction.getGene1().getMembership(
						this)) || Membership.isPresent(interaction.getGene2()
						.getMembership(this)))) {
			return membership;
		}
		return Membership.Missing;
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
		return 0;
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(" :core ");
		print.print(comparison);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}

}
