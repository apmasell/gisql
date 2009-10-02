package ca.wlu.gisql.interactome.tovar;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class ToVar implements Interactome {
	public static final Parseable descriptor = new ToVarDescriptor();

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
		if (Membership.isUndefined(membership)) {
			membership = source.calculateMembership(gene);
			gene.setMembership(this, membership);
		}
		return membership;
	}

	public double calculateMembership(Interaction interaction) {
		double membership = interaction.getMembership(this);
		if (Membership.isUndefined(membership)) {
			membership = source.calculateMembership(interaction);
			interaction.setMembership(this, membership);
		}
		return membership;
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return source.collectAll(set);
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		if (!source.postpare()) {
			return false;
		}
		return environment.setVariable(name, new AstLiteral(
				Type.InteractomeType, this));
	}

	public boolean prepare() {
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, getPrecedence());
		print.print(" @ ");
		print.print(name);
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}

}
