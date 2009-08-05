package ca.wlu.gisql.interactome.tovar;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.ast.AstInteractome;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ToVar implements Interactome {
	public final static Parseable descriptor = new ToVarDescriptor();

	private final Environment environment;

	private final String name;

	private final Interactome source;

	public ToVar(Environment environment, Interactome source, String name) {
		this.environment = environment;
		this.source = source;
		this.name = name;
	}

	public double calculateMembership(Gene gene) {
		double membership = gene.getMembership(this);
		if (GisQL.isUndefined(membership)) {
			membership = source.calculateMembership(gene);
			gene.setMembership(this, membership);
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interaction.getMembership(this);
		if (GisQL.isUndefined(membership)) {
			membership = source.calculateMembership(interaction);
			interaction.setMembership(this, membership);
		}
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		if (!source.postpare())
			return false;
		return environment.setVariable(name, new AstInteractome(this));
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, this.getPrecedence());
		print.print(" @ ");
		print.print(name);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}

}
