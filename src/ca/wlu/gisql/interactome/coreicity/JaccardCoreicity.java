package ca.wlu.gisql.interactome.coreicity;

import java.util.Set;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.ast.Function;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.util.ShowablePrintWriter;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class JaccardCoreicity implements Interactome {

	public static final Function function = new JaccardCoreicityFunction();
	private final Set<Interactome> interactomes;
	private final Interactome source;

	public JaccardCoreicity(Interactome source) {
		this.source = source;
		interactomes = Membership.collectSpecies(this);
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		if (!Membership.isMissing(membership)) {
			int intersection = 0;
			int union = 0;
			for (Interactome species : interactomes) {
				boolean has1 = Membership.isPresent(interaction.getGene1()
						.getMembership(species));
				boolean has2 = Membership.isPresent(interaction.getGene2()
						.getMembership(species));

				if (has1 && has2) {
					intersection++;
				}
				if (has1 || has2) {
					union++;
				}
			}
			if (union == 0) {
				return Membership.Missing;
			} else {
				return (double) intersection / union;
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
		print.print(" :jaccardcore");
	}

	@Override
	public String toString() {
		return ShowableStringBuilder
				.toString(this, Membership.collectAll(this));
	}

}
