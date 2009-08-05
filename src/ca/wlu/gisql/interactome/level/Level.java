package ca.wlu.gisql.interactome.level;

import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Level implements Interactome {
	public static Parseable descriptor = new LevelDescriptor();

	private final Interactome source;

	public Level(Interactome interactome) {
		source = interactome;
	}

	public double calculateMembership(Gene gene) {
		double membership = source.calculateMembership(gene);
		if (GisQL.isPresent(membership)) {
			return 1;
		} else {
			return GisQL.Missing;
		}
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (GisQL.isPresent(membership)) {
			return 1;
		} else {
			return GisQL.Missing;
		}
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		set.add(this);
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return descriptor.getPrecedence();
	}

	public Type getType() {
		return Type.Computed;
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
		print.print(" =");
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
