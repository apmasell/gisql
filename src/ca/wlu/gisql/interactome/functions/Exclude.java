package ca.wlu.gisql.interactome.functions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

@GisqlConstructorFunction(name = "except", description = "Remove a list of genes from an interactome.")
public class Exclude implements Interactome {

	private final Set<Gene> banned = new HashSet<Gene>();

	private final Interactome source;

	public Exclude(Interactome source, List<Gene> genes) {
		super();
		this.source = source;
		banned.addAll(genes);
	}

	@Override
	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		return banned.contains(gene) ? Membership.Missing : membership;
	}

	@Override
	public double calculateMembership(Interaction interaction) {
		return source.calculateMembership(interaction);
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
		return Precedence.UnaryPostfix;
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
		print.print(source, getPrecedence());
		print.print(" :exclude ");
		print.print(banned);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}
}
