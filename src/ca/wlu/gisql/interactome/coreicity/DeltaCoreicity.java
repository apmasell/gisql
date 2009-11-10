package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;
import ca.wlu.gisql.vm.Machine;
import ca.wlu.gisql.vm.Program;

@GisqlConstructorFunction(name = "deltacore", description = "Filter interactions based on the coreicity difference of their genes")
public class DeltaCoreicity implements Interactome {

	private final Program comparison;
	private final Machine machine;
	private final Interactome source;

	public DeltaCoreicity(Machine machine, Interactome source,
			@GisqlType(type = "number → boolean") Program comparison) {
		this.machine = machine;
		this.source = source;
		this.comparison = comparison;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!Membership.isMissing(membership)) {
			long value = Math.abs(interaction.getGene1().getCoreicity()
					- interaction.getGene2().getCoreicity());
			if ((Boolean) comparison.run(machine, value)) {
				return membership;
			}
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

	public Precedence getPrecedence() {
		return Precedence.Value;
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
		print.print(" :deltacore (");
		print.print(comparison);
		print.print(")");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}

}
