package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.annotation.GisqlType;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

@GisqlConstructorFunction(name = "genecoreicity", description = "Filter genes based on their coreicity")
public class GeneCoreicity implements Interactome {

	private final GenericFunction comparison;
	private final Interactome source;

	public GeneCoreicity(
			Interactome source,
			@GisqlType(type = "gene → number → boolean") GenericFunction comparison) {
		this.source = source;
		this.comparison = comparison;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (!Membership.isMissing(membership)
				&& !(Boolean) comparison.run(gene, (long) gene.getCoreicity())) {
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
		print.print(" :genecoreicity (");
		print.print(comparison);
		print.print(")");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}

}
