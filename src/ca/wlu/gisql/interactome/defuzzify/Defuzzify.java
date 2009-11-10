package ca.wlu.gisql.interactome.defuzzify;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

@GisqlConstructorFunction(name = "defuzz", description = "Defuzzify memberships of genes and interactions into crisp 0 and 1.")
public class Defuzzify implements Interactome {

	private final Interactome source;

	public Defuzzify(Interactome interactome) {
		source = interactome;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (Membership.isPresent(membership)) {
			return 1;
		} else {
			return Membership.Missing;
		}
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (Membership.isPresent(membership)) {
			return 1;
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
		return Parser.PREC_LITERAL;
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
		print.print(" :defuzz");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
