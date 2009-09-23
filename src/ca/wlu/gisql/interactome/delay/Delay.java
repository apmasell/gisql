package ca.wlu.gisql.interactome.delay;

import java.util.Set;

import org.apache.log4j.Logger;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.graph.Ubergraph;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class Delay implements Interactome {
	protected static final Logger log = Logger.getLogger(Delay.class);

	private final Interactome source;

	public Delay(final Interactome source) {
		super();
		this.source = source;
	}

	public double calculateMembership(Gene gene) {
		return gene.getMembership(source);
	}

	public double calculateMembership(Interaction interaction) {
		return interaction.getMembership(source);
	}

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return source.collectAll(set);
	}

	public int getPrecedence() {
		return source.getPrecedence();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean postpare() {
		return true;
	}

	public boolean prepare() {
		if (!source.prepare()) {
			log.error("Preparation failed.");
			return false;
		}
		for (Gene gene : Ubergraph.getInstance().genes()) {
			gene.setMembership(source, source.calculateMembership(gene));
		}
		for (Interaction interaction : Ubergraph.getInstance()) {
			interaction.setMembership(source, source
					.calculateMembership(interaction));
		}
		if (!source.postpare()) {
			log.error("Postparation failed.");
			return false;
		}
		return true;
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source);
	}

	public String toString() {
		return ShowableStringBuilder.toString(this, GisQL.collectAll(this));
	}
}
